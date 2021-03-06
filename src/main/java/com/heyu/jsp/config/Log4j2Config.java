package com.heyu.jsp.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.net.URI;

/**
 * log4j2配置类
 *
 * @author heyu
 * @date 2018-09-12
 */
//@Plugin(name = "log4j2Config", category = ConfigurationFactory.CATEGORY)
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@org.springframework.context.annotation.Configuration
public class Log4j2Config extends ConfigurationFactory{

    private static final String INFOAPPENDER = "myInfo";

    private static final String ERRORAPPENDER = "myError";

    private static final String CONSOLE = "Console";

    private static final String ASYNC = "ASYNC";

    private static final String FILEPATH = "/opt/logs/";

	private Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        builder.setStatusLevel(Level.INFO);
        if (true) {
            builder.add(getConsoleBuilder(builder));
            addDevLogger(builder);
            builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef(CONSOLE)));
        } else {
            builder.add(getErrorFileBuilder(builder));
            builder.add(getInfoFileBuilder(builder));
            builder.add(getAsyncBuilder(builder));
            builder.add(builder.newRootLogger(Level.INFO).add(builder.newAppenderRef(ASYNC)));
        }
        return builder.build();
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
        return getConfiguration(loggerContext, source.toString(), null);
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }

	/**
	 * 获取显示台适配器
	 * @param builder
	 * @return
	 */
	private AppenderComponentBuilder getConsoleBuilder(
			ConfigurationBuilder<BuiltConfiguration> builder) {
		AppenderComponentBuilder appenderBuilder = builder.newAppender(
                CONSOLE, "Console").addAttribute("target",
				ConsoleAppender.Target.SYSTEM_OUT);
		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute(
				"pattern",
				"%d{HH:mm:ss} %-5level %logger{36} [%L] - %msg%n"));
		return appenderBuilder;
	}

	/**
	 * 获取Error文件日志适配器
	 * @param builder
	 * @return
	 */
	private AppenderComponentBuilder getInfoFileBuilder(
			ConfigurationBuilder<BuiltConfiguration> builder) {
		AppenderComponentBuilder appenderBuilder = builder
				.newAppender(INFOAPPENDER, "RollingFile")
				.addAttribute("fileName", FILEPATH + "/info/info.log")
				.addAttribute("filePattern", FILEPATH + "/info/info-%d{yyyy-MM-dd}.log");
		appenderBuilder.add(builder.newLayout("ThresholdFilter")
				.addAttribute("level", Level.INFO)
				.addAttribute("onMatch", "ACCEPT")
				.addAttribute("onMismatch", "DENY"));
		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute(
				"pattern",
				"%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} [%L] - %msg%n"));
		appenderBuilder.add(builder.newLayout("Policies").addComponent(
				builder.newLayout("TimeBasedTriggeringPolicy")
						.addAttribute("modulate", true)
						.addAttribute("interval", "1")));
		return appenderBuilder;
	}

	/**
	 * 获取Info文件日志适配器
	 * @param builder
	 * @return
	 */
	private AppenderComponentBuilder getErrorFileBuilder(
			ConfigurationBuilder<BuiltConfiguration> builder) {
		AppenderComponentBuilder appenderBuilder = builder
				.newAppender(ERRORAPPENDER, "RollingFile")
				.addAttribute("fileName",
                        FILEPATH + "/error/error.log")
				.addAttribute("filePattern",
                        FILEPATH + "/error/error-%d{yyyy-MM-dd}.log");
		appenderBuilder.add(builder.newLayout("ThresholdFilter")
				.addAttribute("level", Level.ERROR)
				.addAttribute("onMatch", "ACCEPT")
				.addAttribute("onMismatch", "DENY"));
		appenderBuilder.add(builder.newLayout("PatternLayout").addAttribute(
				"pattern",
				"%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} [%L] - %msg%n"));
		appenderBuilder.add(builder.newLayout("Policies").addComponent(
				builder.newLayout("TimeBasedTriggeringPolicy")
						.addAttribute("modulate", true)
						.addAttribute("interval", "1")));
		return appenderBuilder;
	}

	/**
	 * 获取异步写日志适配器
	 * @param builder
	 * @return
	 */
	private AppenderComponentBuilder getAsyncBuilder(
			ConfigurationBuilder<BuiltConfiguration> builder) {
		AppenderComponentBuilder appenderBuilder = builder
				.newAppender(ASYNC, "Async");
		appenderBuilder.add(builder.newLayout("AppenderRef")
					.addAttribute("ref", INFOAPPENDER));
		appenderBuilder.add(builder.newLayout("AppenderRef")
					.addAttribute("ref", ERRORAPPENDER));
		return appenderBuilder;
	}

	/**
	 * 添加debug环境
	 * @param builder
	 */
	private void addDevLogger(ConfigurationBuilder<BuiltConfiguration> builder){
		// 添加mybatis-debug支持
		LoggerComponentBuilder loggerMyBatisBuilder = builder
				.newLogger("com.pcbwx.jsp.dao", Level.DEBUG)
				.addAttribute("additivity", false)
				.add(builder.newAppenderRef(CONSOLE));
		builder.add(loggerMyBatisBuilder);

        // 添加spring-debug支持
		LoggerComponentBuilder loggerSpringBuilder = builder
				.newLogger("org.springframework", Level.INFO)
				.addAttribute("additivity", false)
				.add(builder.newAppenderRef(CONSOLE));
		builder.add(loggerSpringBuilder);
	}
}
