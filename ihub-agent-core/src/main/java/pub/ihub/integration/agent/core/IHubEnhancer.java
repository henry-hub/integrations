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
package pub.ihub.integration.agent.core;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;

/**
 * @author henry
 * @since 2024/4/2
 */
public interface IHubEnhancer<T> extends IAspectEnhancer, IAspectDefinition<T> {

	@Override
	default ElementMatcher.Junction<T> enhanceClass() {
		return null;
	}

	@Override
	default ElementMatcher<MethodDescription> getMethodsMatcher() {
		return null;
	}

	@Override
	default String getMethodsEnhancer() {
		return null;
	}

	@Override
	default void beforeMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result) throws Throwable {

	}

	@Override
	default Object afterMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
		return null;
	}

	@Override
	default void handleMethodException(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

	}

}
