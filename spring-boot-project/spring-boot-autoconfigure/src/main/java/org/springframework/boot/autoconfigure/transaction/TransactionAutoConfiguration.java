/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.transaction;

import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.AbstractTransactionManagementConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} for Spring transaction.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Configuration(proxyBeanMethods = false)
// xjh-表示只有在类路径中存在PlatformTransactionManager类时，此TransactionAutoConfiguration才会加入容器
@ConditionalOnClass(PlatformTransactionManager.class)
@AutoConfigureAfter({ JtaAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, Neo4jDataAutoConfiguration.class })
@EnableConfigurationProperties(TransactionProperties.class)
public class TransactionAutoConfiguration {

	// xjh-@ConditionalOnMissingBean作用：在BeanFactory中没有TransactionManagerCustomizers类型的bean存在时，则使用下面方法生成的bean
	@Bean
	@ConditionalOnMissingBean
	public TransactionManagerCustomizers platformTransactionManagerCustomizers(
			ObjectProvider<PlatformTransactionManagerCustomizer<?>> customizers) {
		return new TransactionManagerCustomizers(customizers.orderedStream().collect(Collectors.toList()));
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnSingleCandidate(ReactiveTransactionManager.class)
	public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
		return TransactionalOperator.create(transactionManager);
	}

	// xjh-@ConditionalOnSingleCandidate(PlatformTransactionManager.class)表示只有在容器中已经存在唯一的PlatformTransactionManager类型的bean时满足要求
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnSingleCandidate(PlatformTransactionManager.class)
	public static class TransactionTemplateConfiguration {

		@Bean
		@ConditionalOnMissingBean(TransactionOperations.class)
		public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
			// xjh-创建一个TransactionTemplate
			return new TransactionTemplate(transactionManager);
		}

	}

	// xjh-当容器中不存在AbstractTransactionManagementConfiguration类型的bean时，创建一个EnableTransactionManagementConfiguration。
	// 使用EnableTransactionManagementConfiguration即是使用springAop的jdk或者cglib代理。
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnBean(TransactionManager.class)
	@ConditionalOnMissingBean(AbstractTransactionManagementConfiguration.class)
	public static class EnableTransactionManagementConfiguration {

		// xjh-注意下面的@EnableTransactionManagement注解，此注解通过@Import引入TransactionManagementConfigurationSelector类，此类引入了事务相关的增强器(SpringAop或者AspectJ)
		// 详情查看Spring源码TransactionManagementConfigurationSelector
		// 通过@ConditionalOnProperty注解，读取用户选择cglib还是jdk动态代理作为事务增强器，默认使用cglib。
		@Configuration(proxyBeanMethods = false)
		@EnableTransactionManagement(proxyTargetClass = false)
		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false",
				matchIfMissing = false)
		public static class JdkDynamicAutoProxyConfiguration {

		}

		@Configuration(proxyBeanMethods = false)
		@EnableTransactionManagement(proxyTargetClass = true)
		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
				matchIfMissing = true)
		public static class CglibAutoProxyConfiguration {

		}

	}

}
