/*
 * Copyright (c) 2021 Henry 李恒 (henry.box@outlook.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pub.ihub.integration.process.boot;

import cn.hutool.core.io.file.PathUtil;
import com.google.auto.service.AutoService;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor;
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType;
import pub.ihub.integration.process.core.BaseJavapoetProcessor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.Element;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.SourceVersion.RELEASE_17;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

/**
 * 自动配置处理器
 *
 * @author henry
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_17)
@SupportedAnnotationTypes("org.springframework.boot.autoconfigure.AutoConfiguration")
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
public class AutoConfigurationProcessor extends BaseJavapoetProcessor {

	protected static final String IMPORTS_RESOURCE = "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";
	protected final Set<String> imports = new HashSet<>();

	@Override
	protected void processElement(Element element) {
		imports.add(element.getEnclosingElement().toString() + "." + element.getSimpleName());
	}

	@Override
	protected void processingOver() throws IOException {
		String resources = mFiler.getResource(CLASS_OUTPUT, "", IMPORTS_RESOURCE).getName()
			.replace("classes\\java", "resources");
		if (PathUtil.exists(Path.of(resources), true)) {
			note("The %s is exists, ignore generate %s.", resources, IMPORTS_RESOURCE);
			return;
		}
		// 生成spring.factories
		List<String> lines = new ArrayList<>();
		lines.add("# Generated by ihub-process https://ihub.pub");
		lines.addAll(imports);
		writeResource(CLASS_OUTPUT, IMPORTS_RESOURCE, lines);
		// annotationProcessor也保存一份便于代码阅读
		writeResource(SOURCE_OUTPUT, IMPORTS_RESOURCE, lines);
	}

}
