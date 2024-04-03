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

import pub.ihub.integration.agent.core.IHubEnhancer;
import pub.ihub.integration.core.Logger;

import java.lang.reflect.Method;

/**
 * Created by pphh on 2022/8/4.
 */
public class TomcatEnhancer implements IHubEnhancer<Object> {

	@Override
	public void beforeMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result) throws Throwable {
		Logger.info("[trace]beforeMethod(), method = %s.%s", method.getDeclaringClass().getName(), method.getName());
	}

	@Override
	public Object afterMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result) throws Throwable {
		Logger.info("[trace]afterMethod(), method = %s.%s", method.getDeclaringClass().getName(), method.getName());
		return null;
	}

	@Override
	public void handleMethodException(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {
		Logger.info("[trace]handleMethodException(), method = %s.%s", method.getDeclaringClass().getName(), method.getName());
	}

}
