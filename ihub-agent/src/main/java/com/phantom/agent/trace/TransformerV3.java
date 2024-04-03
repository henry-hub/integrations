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
package com.phantom.agent.trace;

import com.phantom.agent.enhancer.EnhancerProxy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;
import pub.ihub.integration.agent.core.IAspectDefinition;
import pub.ihub.integration.agent.core.IAspectEnhancer;
import pub.ihub.integration.agent.core.loader.EnhancerInstanceLoader;
import pub.ihub.integration.core.Logger;

import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * Created by pphh on 2022/8/5.
 * 通过当前Transformer对类进行字节码增强，通过EnhancerInstanceLoader创建enhancer实例，解决了NoClassDefFoundError问题。
 * 具体原因分析见博文：
 */
public class TransformerV3 implements AgentBuilder.Transformer {

	public String aspectClz;

	public TransformerV3(String aspectClz) {
		this.aspectClz = aspectClz;
	}

	@Override
	public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription,
											ClassLoader loader, JavaModule javaModule, ProtectionDomain protectionDomain) {
		Logger.info("transformV3 %s...", typeDescription.getTypeName());

		EnhancerProxy proxy = new EnhancerProxy();
		ElementMatcher<MethodDescription> methodsMatcher = null;
		try {
			IAspectDefinition aspectDefinition = EnhancerInstanceLoader.load(this.aspectClz, loader);
			methodsMatcher = aspectDefinition.getMethodsMatcher();

			String enhancerClz = aspectDefinition.getMethodsEnhancer();
			IAspectEnhancer enhancer = EnhancerInstanceLoader.load(enhancerClz, loader);
			proxy.setEnhancer(enhancer);
		} catch (Exception e) {
			Logger.error("failed to initialized the proxy %s", e.toString());
		}

		if (methodsMatcher == null) {
			Logger.error("methodsMatcher is null");
			return null;
		}

		ElementMatcher.Junction<MethodDescription> junction = not(isStatic()).and(methodsMatcher);
		return builder.method(junction)
			.intercept(MethodDelegation.withDefaultConfiguration()
				.withBinders(Morph.Binder.install(OverrideCallable.class))
				.to(proxy));
	}

}
