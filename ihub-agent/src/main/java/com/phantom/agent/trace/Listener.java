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

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import pub.ihub.integration.core.Logger;

/**
 * Created by pphh on 2022/8/4.
 */
public class Listener implements AgentBuilder.Listener {
	@Override
	public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean bLoaded) {
		Logger.debug("Enhance class {%s} onDiscovery, loaded = %s", typeName, bLoaded);
	}

	@Override
	public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean bLoaded, DynamicType dynamicType) {
		Logger.debug("On Transformation class {%s}, loaded = %s", typeDescription.getName(), bLoaded);
	}

	@Override
	public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean bLoaded) {
		Logger.debug("Enhance class {%s} onIgnored, loaded = %s", typeDescription, bLoaded);
	}

	@Override
	public void onError(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean bLoaded, Throwable throwable) {
		Logger.error("Enhance class {%s} error, loaded = %s, exception msg = %s", typeName, bLoaded, throwable.getMessage());
	}

	@Override
	public void onComplete(String typeName, ClassLoader classLoader, JavaModule javaModule, boolean bLoaded) {
		Logger.debug("Enhance class {%s} onComplete, loaded = %s", typeName, bLoaded);
	}
}
