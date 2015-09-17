package com.august.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * PROJECT_NAME: core
 * PACKAGE_NAME: com.august.config
 * Author: August
 * Update: August(2015/8/23)
 * Description:创建//加入AspectJ的动态代理（非必需）
 */
@Configuration//表示该类是一个配置文件 //标注此类为配置类（必有）
//加入AspectJ的动态代理，替换掉下面的注释部分的注入
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Aspect
public class AopConfig {
    //定义日志记录器
    private static final Logger LOGGER = LoggerFactory.getLogger(AopConfig.class);


    /**
     * <tx:advice id="transactionAdvice" transaction-manager="transactionManager">
     * <tx:attributes>
     * <tx:method name="*" propagation="REQUIRED" rollback-for="Exception"/>
     * </tx:attributes>
     * </tx:advice>
     * <p>
     * //激活自动代理功能
     * <aop:config proxy-target-class="true">
     * <aop:pointcut id="transactionPointcut" expression="execution(* com.handu.base.**.service.*.*(..))"/>
     * <aop:advisor pointcut-ref="transactionPointcut" advice-ref="transactionAdvice"/>
     * </aop:config>
     */
    //配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
    @Pointcut("execution(* com.august.**.service..*(..))")
    public void aspect() {
    }

    /*
     * 配置前置通知,使用在方法aspect()上注册的切入点
     * 同时接受JoinPoint切入点对象,可以没有该参数
     * 在核心业务执行前执行，不能阻止核心业务的调用。
     */
    @Before("aspect()")
    public void before(JoinPoint joinPoint) {
        System.out.println("-----beforeAdvice().invoke-----");
        System.out.println(" 此处意在执行核心业务逻辑前，做一些安全性的判断等等");
        System.out.println(" 可通过joinPoint来获取所需要的内容");
        System.out.println("-----End of beforeAdvice()------");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("before " + joinPoint);
        }
    }


    /**
     * 配置后置通知,使用在方法aspect()上注册的切入点
     * 核心业务逻辑退出后（包括正常执行结束和异常退出），执行此Advice
     *
     * @param joinPoint
     */
    @After("aspect()")
    public void after(JoinPoint joinPoint) {
        System.out.println("-----afterAdvice().invoke-----");
        System.out.println(" 此处意在执行核心业务逻辑之后，做一些日志记录操作等等");
        System.out.println(" 可通过joinPoint来获取所需要的内容");
        System.out.println("-----End of afterAdvice()------");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("after " + joinPoint);
        }
    }


    /**
     * 配置环绕通知,使用在方法aspect()上注册的切入点
     * 手动控制调用核心业务逻辑，以及调用前和调用后的处理,
     * <p>
     * 注意：当核心业务抛异常后，立即退出，转向AfterAdvice
     * 执行完AfterAdvice，再转到ThrowingAdvice
     *
     * @param joinPoint
     */
    @Around("aspect()")
    public void around(JoinPoint joinPoint) {
        System.out.println("-----aroundAdvice().invoke-----");
        System.out.println(" 此处可以做类似于Before Advice的事情");

        //调用核心逻辑
        //…………
        System.out.println(" 此处可以做类似于After Advice的事情");
        System.out.println("-----End of aroundAdvice()------");
        long start = System.currentTimeMillis();
        try {
            ((ProceedingJoinPoint) joinPoint).proceed();
            long end = System.currentTimeMillis();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("around " + joinPoint + "\tUse time : " + (end - start) + " ms!");
            }
        } catch (Throwable e) {
            long end = System.currentTimeMillis();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("around " + joinPoint + "\tUse time : " + (end - start) + " ms with exception : " + e.getMessage());
            }
        }
    }

    /**
     * 配置后置返回通知,使用在方法aspect()上注册的切入点
     * 核心业务逻辑调用正常退出后，不管是否有返回值，正常退出后，均执行此Advice
     */
    @AfterReturning("aspect()")
    public void afterReturn(JoinPoint joinPoint) {
        System.out.println("-----afterReturningAdvice().invoke-----");
        System.out.println(" 此处可以对返回值做进一步处理");
        System.out.println(" 可通过joinPoint来获取所需要的内容");
        System.out.println("-----End of afterReturningAdvice()------");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("afterReturn " + joinPoint);
        }
    }

    /**
     * 配置抛出异常后通知,使用在方法aspect()上注册的切入点
     * 核心业务逻辑调用异常退出后，执行此Advice，处理错误信息
     * <p>
     * 注意：执行顺序在Around Advice之后
     */
    @AfterThrowing(pointcut = "aspect()", throwing = "ex")
    public void afterThrow(JoinPoint joinPoint, Exception ex) {
        System.out.println("-----afterThrowingAdvice().invoke-----");
        System.out.println(" 错误信息：" + ex.getMessage());
        System.out.println(" 此处意在执行核心业务逻辑出错时，捕获异常，并可做一些日志记录操作等等");
        System.out.println(" 可通过joinPoint来获取所需要的内容");
        System.out.println("-----End of afterThrowingAdvice()------");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("afterThrow " + joinPoint + "\t" + ex.getMessage());
        }
    }
    /*
     *
	 * <!-- 激活自动代理功能 参看：web.function.aop.aspect.DemoAspect -->
	 * <aop:aspectj-autoproxy proxy-target-class="true" />
	 *
	 * @EnableAspectJAutoProxy(proxyTargetClass=true) 与声明下面的bean作用相同
	 */
//	@Bean
//	public AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator() {
//		LOGGER.info("AnnotationAwareAspectJAutoProxyCreator");
//		AnnotationAwareAspectJAutoProxyCreator aspectJAutoProxyCreator = new AnnotationAwareAspectJAutoProxyCreator();
//		// false:使用JDK动态代理织入增强 [基于目标类的接口] true:使用CGLib动态代理织入增强[基于目标类]
//		aspectJAutoProxyCreator.setProxyTargetClass(true);
//		return aspectJAutoProxyCreator;
//	}
}
