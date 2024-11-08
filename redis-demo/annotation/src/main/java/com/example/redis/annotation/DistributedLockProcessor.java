package com.example.redis.annotation;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class DistributedLockProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(DistributedLock.class)) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) element;
                var parameters = method.getParameters();
                if (!isValidLockKeyParam(parameters)) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "@DistributedLock 메서드에는 반드시 lockKey 첫번째 파라미터가 필요합니다.",
                            method);
                }
            }
        }
        return true;
    }

    public boolean isValidLockKeyParam(List<? extends VariableElement> parameters) {
        if (parameters.isEmpty())
            return false;
        return parameters.get(0).getSimpleName().toString().equals("lockKey");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        System.out.println("supported type:" + DistributedLock.class.getPackageName());
        supportedAnnotationTypes.add(DistributedLock.class.getName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
