package junitparams.internal;

import junitparams.naming.TestCaseNamingStrategy;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

class ParametrizedDescription {

    private TestCaseNamingStrategy namingStrategy;
    private String testClassName;
    private String methodName;
    private FrameworkMethod frameworkMethod;

    ParametrizedDescription(TestCaseNamingStrategy namingStrategy, String testClassName, String methodName) {
        this.namingStrategy = namingStrategy;
        this.testClassName = testClassName;
        this.methodName = methodName;
    }
    ParametrizedDescription(TestCaseNamingStrategy namingStrategy,
                            String testClassName, String methodName ,FrameworkMethod frameworkMethod) {
        this.namingStrategy = namingStrategy;
        this.testClassName = testClassName;
        this.methodName = methodName;
        this.frameworkMethod = frameworkMethod;
    }

    Description parametrizedDescription(Object[] params) {
        Description parametrised = Description.createSuiteDescription(methodName) ;
        for (int i = 0; i < params.length; i++) {
            Object paramSet = params[i];
            String name = namingStrategy.getTestCaseName(i, paramSet);
            String uniqueMethodId = Utils.uniqueMethodId(i, paramSet, methodName);
            Description mDescription = null;
            if (frameworkMethod != null) {
                try {
                    Constructor<Description> descriptionConstructor = Description.class.getDeclaredConstructor
                            (Class.class, String.class, Serializable.class, Annotation[].class);
                    descriptionConstructor.setAccessible(true);
                    mDescription = descriptionConstructor.newInstance(null,
                            String.format("%s(%s)", name, testClassName),
                            uniqueMethodId,
                            frameworkMethod.getAnnotations());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            parametrised.addChild(
                    mDescription == null ?
                    Description.createTestDescription(testClassName, name, uniqueMethodId ) : mDescription);
        }
        return parametrised;
    }
}
