To install into the ServiceMix 4.4.0 OSGi container:

1) Start ServiceMix
2) Run the following commands:

osgi:install -s mvn:org.jboss.resteasy.osgi/org-jboss-resteasy-core/2.2.3.GA-r001
osgi:install -s mvn:org.jboss.resteasy.osgi/org-jboss-resteasy-service-api/0.0.1
osgi:install -s mvn:org.jboss.resteasy.osgi/org-jboss-resteasy-service/0.0.1

## OR ##

osgi:install -s file:C:\\tmp\\org-jboss-resteasy-core-2.2.3.GA-r001.jar
osgi:install -s file:C:\\tmp\\org-jboss-resteasy-service-api-0.0.1.jar
osgi:install -s file:C:\\tmp\\org-jboss-resteasy-service-0.0.1.jar

