/*
 * Copyright (c) 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phantom.agent;

import com.phantom.agent.enhancer.AbstractEnhancer;
import com.phantom.agent.enhancer.impl.TomcatEnhancer;
import com.phantom.agent.trace.*;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import pub.ihub.integration.agent.core.context.IHubTraceContext;
import pub.ihub.integration.core.Logger;

import java.lang.instrument.Instrumentation;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Created by pphh on 2022/8/4.
 */
public class PhantomAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		Logger.info("The phantom agent start to load...");

		// load the tracing context
		List<SpanExporter> spanExporterList = new ArrayList<>();
		spanExporterList.add(new LoggingSpanExporter());
		IHubTraceContext.initTraceContext(spanExporterList);

		// load the tracing aspect
		final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(false));

		AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy).ignore(
			nameStartsWith("net.bytebuddy.")
				.or(nameStartsWith("org.slf4j."))
				.or(nameStartsWith("org.groovy."))
				.or(nameContains("javassist"))
				.or(nameContains(".asm."))
				.or(nameContains(".reflectasm."))
				.or(nameStartsWith("sun.reflect"))
				.or(ElementMatchers.isSynthetic()));

		Listener listener = new Listener();

		Properties systemProperties = System.getProperties();
		String transformerVer = systemProperties.getProperty("agent.transformer.version");
		if (transformerVer == null) {
			transformerVer = "v1";
		}

		if (transformerVer.equals("v1")) {
			List<AbstractEnhancer> enhancerList = new ArrayList<>();
			enhancerList.add(new TomcatEnhancer());

			Logger.info("load transformer v1.");
			for (AbstractEnhancer enhancer : enhancerList) {
				ElementMatcher.Junction matcher = enhancer.enhanceClass();
				agentBuilder.type(matcher)
					.transform(new TransformerV1(enhancer))
					.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
					.with(listener)
					.installOn(inst);
			}
		} else if (transformerVer.equals("v2")) {
			Map<String, String> aspectContexts = new HashMap<>();
			aspectContexts.put("org.apache.catalina.core.StandardHostValve", "com.phantom.agent.enhancer.impl.TomcatEnhancer");

			Logger.info("load transformer v2.");
			for (Map.Entry<String, String> aspectEntry : aspectContexts.entrySet()) {
				String enhanceAspect = aspectEntry.getKey();
				String enhanceClass = aspectEntry.getValue();
				ElementMatcher.Junction matcher = named(enhanceAspect).and(not(isInterface()));
				agentBuilder.type(matcher)
					.transform(new TransformerV2(enhanceClass))
					.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
					.with(listener)
					.installOn(inst);
			}
		} else {
			Map<String, String> aspectContexts = new HashMap<>();
			aspectContexts.put("org.apache.catalina.core.StandardHostValve", "com.phantom.plugin.TomcatAspect");

			Logger.info("load transformer v3.");
			for (Map.Entry<String, String> aspectEntry : aspectContexts.entrySet()) {
				String enhanceClass = aspectEntry.getKey();
				String enhanceAspect = aspectEntry.getValue();
				ElementMatcher.Junction matcher = named(enhanceClass).and(not(isInterface()));
				agentBuilder.type(matcher)
					.transform(new TransformerV3(enhanceAspect))
					.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
					.with(listener)
					.installOn(inst);
			}
		}

		Logger.info("The phantom agent has been loaded.");
	}
}
