package com.heldiam.jrpcx.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author kinwyb
 * @date 2019-06-21 09:21
 **/
public abstract class AnnotatedBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

    private ConcurrentMap<String, AnnotatedInjectionMetadata> injectionMetadataCache = new ConcurrentHashMap();

    private static final Logger logger = LoggerFactory.getLogger(AnnotatedBeanPostProcessor.class.getName());

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        //这个是注入元数据，包含了目标Bean的Class对象，和注入元素（InjectionElement）集合
        InjectionMetadata metadata = findAnnotatedMetadata(beanName, bean.getClass(), pvs);
        try {
            // 通过反射来给bean设置值了
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of Annotated dependencies failed", ex);
        }
        return pvs;
    }

    private InjectionMetadata findAnnotatedMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // 通过类名作为缓存的key
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // 从缓存中的injectionMetadataCache根据类名获取元数据
        AnnotatedInjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildAnnotatedMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect object class [" + clazz.getName() +
                                "] for annotation metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    private AnnotatedInjectionMetadata buildAnnotatedMetadata(final Class<?> beanClass) {
        // 获取属性上的注解
        Collection<AnnotatedFieldElement> fieldElements = findFieldAnnotatedMetadata(beanClass);
        return new AnnotatedInjectionMetadata(beanClass, fieldElements);
    }

    private List<AnnotatedFieldElement> findFieldAnnotatedMetadata(final Class<?> beanClass) {
        final List<AnnotatedFieldElement> elements = new LinkedList<>();
        // 通过反射的工具类，获取当前beanClass的所有Filed
        ReflectionUtils.doWithFields(beanClass, field -> {
            if (Modifier.isStatic(field.getModifiers())) {
                if (logger.isWarnEnabled()) {
                    logger.warn("不支持静态字段注解: " + field);
                }
                return;
            }
            // 获取Reference注解
            Object injectionObject = getInjectionObject(field, beanClass);
            // 注解不为空
            if (injectionObject != null) {
                // 构建ReferenceFieldElement
                elements.add(new AnnotatedFieldElement(field, injectionObject));
            }

        });
        return elements;
    }

    /**
     * 获取注入对象
     *
     * @param beanClass
     * @return
     */
    protected abstract Object getInjectionObject(Field field, final Class<?> beanClass);

    private class AnnotatedFieldElement extends InjectionMetadata.InjectedElement {
        // 字段对象
        private final Field field;
        // 注解对象
        private final Object reference;

        protected AnnotatedFieldElement(Field field, Object reference) {
            super(field, null);
            this.field = field;
            this.reference = reference;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            // 字段为私有，需要设置这个属性field.setAccessible(true)  才能进行设值
            ReflectionUtils.makeAccessible(field);
            // 给这个对象bean的这个filed设置值，值为：referenceBean.getObject()
            field.set(bean, reference);
        }

    }

    private class AnnotatedInjectionMetadata extends InjectionMetadata {

        private final Collection<AnnotatedFieldElement> fieldElements;

        public AnnotatedInjectionMetadata(Class<?> targetClass, Collection<AnnotatedFieldElement> fieldElements) {
            super(targetClass, combine(fieldElements));
            this.fieldElements = fieldElements;
        }

        public Collection<AnnotatedFieldElement> getFieldElements() {
            return fieldElements;
        }

    }

    @SafeVarargs
    private static <T> Collection<T> combine(Collection<? extends T>... elements) {
        List<T> allElements = new ArrayList<>();
        for (Collection<? extends T> e : elements) {
            allElements.addAll(e);
        }
        return allElements;
    }

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
