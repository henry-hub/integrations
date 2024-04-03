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

import net.bytebuddy.implementation.bind.annotation.*;
import pub.ihub.integration.agent.core.IAspectEnhancer;
import pub.ihub.integration.core.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Created by pphh on 2022/8/4.
 */
public class EnhancerProxy {

	public IAspectEnhancer enhancer;

	@IgnoreForBinding
	public void setEnhancer(IAspectEnhancer enhancer) {
		this.enhancer = enhancer;
	}

	/**
	 * Intercept the target instance method with buddybyte
	 *
	 * @param obj          target class instance.
	 * @param allArguments all method arguments
	 * @param method       method description.
	 * @param zuper        the origin call ref.
	 * @return the return value of target instance method.
	 * @throws Exception only throw exception because of zuper.call() or unexpected exception in sky-walking ( This is a
	 *                   bug, if anything triggers this condition ).
	 */
	@RuntimeType
	@BindingPriority(value = 1)
	public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @SuperCall Callable<?> zuper,
							@Origin Method method) throws Throwable {
		Object result = null;
		try {
			enhancer.beforeMethod(obj, method, allArguments, method.getParameterTypes(), result);
		} catch (Throwable t) {
			Logger.error("EnhancerProxy failure - beforeMethod, [%s].[%s], msg = %s", obj.getClass(), method.getName(), t.toString());
		}

		Object ret = null;
		try {
			if (null != result) {
				ret = result;
			} else {
				ret = zuper.call();
			}
		} catch (Throwable t) {
			try {
				enhancer.handleMethodException(obj, method, allArguments, method.getParameterTypes(), t);
			} catch (Throwable t2) {
				Logger.error("EnhancerProxy failure - handleMethodException, [%s].[%s], msg = %s", obj.getClass(), method.getName(), t2.toString());
			}
			throw t;
		} finally {
			try {
				enhancer.afterMethod(obj, method, allArguments, method.getParameterTypes(), ret);
			} catch (Throwable t3) {
				Logger.error("EnhancerProxy failure - afterMethod, [%s].[%s], msg = %s", obj.getClass(), method.getName(), t3.toString());
			}
		}

		return ret;

	}
}
