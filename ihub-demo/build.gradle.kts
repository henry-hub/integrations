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
import net.bytebuddy.build.gradle.AbstractByteBuddyTask
import net.bytebuddy.build.gradle.Adjustment
import net.bytebuddy.build.gradle.Discovery
import pub.ihub.integration.bytebuddy.core.IHubPlugin

buildscript {
    dependencies {
        classpath("pub.ihub.integration:ihub-bytebuddy-plugin:main-SNAPSHOT")
    }
}

plugins {
    application
    id("pub.ihub.plugin.ihub-boot")
//    id("com.ryandens.javaagent-application") version "0.5.1"
    alias(ihub.plugins.javaagent)
//    id("com.github.johnrengelman.shadow").version("8.1.1")
//    alias(ihub.plugins.shadow)
//    id("net.bytebuddy.byte-buddy-gradle-plugin")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
//    javaagent(project(":ihub-agent", "shadow"))
//    javaagent("pub.ihub.integration:ihub-agent:main-SNAPSHOT")
}

iHubJavaagent {
    javaagent="pub.ihub.integration:ihub-agent-plugin:main-SNAPSHOT"
}

byteBuddy {
//extensions.configure(AbstractByteBuddyTaskExtension::class.java) {
//    transformation {
//        plugin = IHubPlugin::class.java
//    }
//    println(transformations)
//    discovery = Discovery.UNIQUE
//    discoverySet = files("META-INF/net.bytebuddy/build.plugins")
    adjustment = Adjustment.ACTIVE
}

tasks.withType(AbstractByteBuddyTask::class.java).configureEach {
    tasks.getByName("classes").dependsOn(this)
}
