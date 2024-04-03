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

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;
import pub.ihub.integration.agent.core.IHubEnhancer;
import pub.ihub.integration.agent.core.IHubEnhancerProxy;
import pub.ihub.integration.core.Logger;

import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * @author henry
 * @since 2024/3/31
 */
public class Transformer implements AgentBuilder.Transformer {
	public IHubEnhancer enhancer;

	public Transformer(IHubEnhancer iAspect) {
		this.enhancer = iAspect;
	}

	@Override
	public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, ProtectionDomain protectionDomain) {
		Logger.info("transformV1 %s...", typeDescription.getTypeName());
		IHubEnhancerProxy proxy = new IHubEnhancerProxy();
		proxy.setEnhancer(enhancer);

		ElementMatcher.Junction<MethodDescription> junction = not(isStatic()).and(enhancer.getMethodsMatcher());
		return builder.method(junction)
			.intercept(MethodDelegation.withDefaultConfiguration()
				.withBinders(Morph.Binder.install(OverrideCallable.class))
				.to(proxy));
	}
}
