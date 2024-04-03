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
package com.phantom.agent.enhancer;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import pub.ihub.integration.agent.core.IAspectDefinition;
import pub.ihub.integration.agent.core.IAspectEnhancer;

import java.lang.reflect.Method;

/**
 * Created by pphh on 2022/8/4.
 */
public class AbstractEnhancer implements IAspectEnhancer, IAspectDefinition {

    @Override
    public ElementMatcher.Junction enhanceClass() {
        return null;
    }

    @Override
    public ElementMatcher<MethodDescription> getMethodsMatcher() {
        return null;
    }

    @Override
    public String getMethodsEnhancer() {
        return null;
    }

    @Override
    public void beforeMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result) throws Throwable {

    }

    @Override
    public Object afterMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        return null;
    }

    @Override
    public void handleMethodException(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }

}
