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
package com.phantom.plugin;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import pub.ihub.integration.core.Logger;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.nameContains;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

/**
 * @author henry
 * @since 2024/3/31
 */
public class PhantomAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		Logger.info("Phantom agent premain start");

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

		List<TomcatEnhancer> enhancerList = new ArrayList<>();
		enhancerList.add(new TomcatEnhancer());

		Logger.info("load transformer v1.");
		for (TomcatEnhancer enhancer : enhancerList) {
			agentBuilder.type(enhancer.enhanceClass())
				.transform(new Transformer(enhancer))
				.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
				.with(listener)
				.installOn(inst);
		}
	}

}
