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

import com.phantom.agent.enhancer.AbstractEnhancer;
import com.phantom.agent.enhancer.EnhancerProxy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaModule;
import pub.ihub.integration.core.Logger;

import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * Created by pphh on 2022/8/4.
 * 通过当前Transformer对类进行字节码增强，虽然通过方法中的classLoader创建enhancer实例，但仍然会存现NoClassDefFoundError问题。
 * 具体原因分析见博文：
 */
public class TransformerV2 implements AgentBuilder.Transformer {
    public String enhanceClass;

    public TransformerV2(String enhanceClass) {
        this.enhanceClass = enhanceClass;
    }

    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader loader, JavaModule javaModule, ProtectionDomain protectionDomain) {
        Logger.info("transformV2 %s...", typeDescription.getTypeName());

        EnhancerProxy proxy = new EnhancerProxy();
        ElementMatcher<MethodDescription> methodsMatcher = null;
        try {
            AbstractEnhancer enhancer = (AbstractEnhancer) Class.forName(this.enhanceClass, true, loader).newInstance();
            methodsMatcher = enhancer.getMethodsMatcher();
            proxy.setEnhancer(enhancer);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
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
