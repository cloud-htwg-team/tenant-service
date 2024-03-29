FROM hseeberger/scala-sbt:11.0.14.1_1.6.2_2.13.8 as builder
COPY . .
RUN sbt assembly

#FROM azul/zulu-openjdk-alpine:11 as packager
#
#RUN { \
#        java --version ; \
#        echo "jlink version:" && \
#        jlink --version ; \
#    }
#
#ENV JAVA_MINIMAL=/opt/jre
#
#RUN jlink \
#    --verbose \
#    --add-modules \
#        java.base,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument,jdk.unsupported \
#        # java.naming - javax/naming/NamingException
#        # java.desktop - java/beans/PropertyEditorSupport
#        # java.management - javax/management/MBeanServer
#        # java.security.jgss - org/ietf/jgss/GSSException
#        # java.instrument - java/lang/instrument/IllegalClassFormatException
#    --compress 2 \
#    --strip-debug \
#    --no-header-files \
#    --no-man-pages \
#    --output "$JAVA_MINIMAL"


#FROM alpine

#ENV JAVA_MINIMAL=/opt/jre
#ENV PATH="$PATH:$JAVA_MINIMAL/bin"

#COPY --from=packager "$JAVA_MINIMAL" "$JAVA_MINIMAL"
FROM sapmachine:11
COPY --from=builder "/root/target/scala-2.13/tenant-service-assembly-0.1.jar" "/app.jar"
ENV GOOGLE_APPLICATION_CREDENTIALS=/gcloud/credentials
COPY config/credentials /gcloud/credentials

EXPOSE 8888
CMD [ "-jar", "/app.jar" ]
ENTRYPOINT [ "java" ]
