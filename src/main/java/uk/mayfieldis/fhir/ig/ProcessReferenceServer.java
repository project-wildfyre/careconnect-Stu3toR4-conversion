    package uk.mayfieldis.fhir.ig;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.convertors.VersionConvertor_30_40;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.utilities.cache.NpmPackage;
import org.hl7.fhir.utilities.cache.PackageCacheManager;
import org.hl7.fhir.utilities.cache.ToolsVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProcessReferenceServer {

    private String serverUrl;

    private Map<String, IBaseResource> resources =  new HashMap<>();

    private IGenericClient client;


    VersionConvertor_30_40 convertor = new VersionConvertor_30_40();


    FhirContext ctx = FhirContext.forDstu3();

    private static Logger log = LoggerFactory
            .getLogger(ProcessReferenceServer.class);



    public ProcessReferenceServer(String packageId, String version) {
        try {
             PackageCacheManager pcm = new PackageCacheManager(true, ToolsVersion.TOOLS_VERSION);

            log.info("Loading Ig Package for API Conformance {} {}",packageId,version);
            NpmPackage npm = pcm.loadPackage(packageId,version);

            for (String uri : npm.listResources("StructureDefinition","ValueSet","CodeSystem","ConceptMap","NamingSystem", "CapabilityStatement")) {


                IBaseResource resource = ctx.newJsonParser().parseResource(npm.load("package", uri));

                String convertedResource = ctx.newJsonParser().encodeResourceToString(resource);
                convertedResource = convertedResource.replace("https://fhir.hl7.org.uk/STU3","https://hl7.org.uk/fhir");

                IBaseResource resourceR4 = convertsToR4(ctx.newJsonParser().parseResource(convertedResource));
                if (resourceR4 instanceof org.hl7.fhir.r4.model.StructureDefinition) {
                    org.hl7.fhir.r4.model.StructureDefinition dom = (org.hl7.fhir.r4.model.StructureDefinition) resourceR4;
                    resources.put(dom.getUrl(),dom);
                }
                if (resourceR4 instanceof org.hl7.fhir.r4.model.ValueSet) {
                    org.hl7.fhir.r4.model.ValueSet dom = (org.hl7.fhir.r4.model.ValueSet) resourceR4;
                    resources.put(dom.getUrl(),dom);
                }
                if (resourceR4 instanceof org.hl7.fhir.r4.model.CodeSystem) {
                    org.hl7.fhir.r4.model.CodeSystem dom = (org.hl7.fhir.r4.model.CodeSystem) resourceR4;
                    resources.put(dom.getUrl(),dom);
                }
                if (resourceR4 instanceof org.hl7.fhir.r4.model.NamingSystem) {
                    org.hl7.fhir.r4.model.NamingSystem dom = (org.hl7.fhir.r4.model.NamingSystem) resourceR4;
                    //resources.put(dom.getUrl(),dom);
                }
                if (resourceR4 instanceof org.hl7.fhir.r4.model.ConceptMap) {
                    org.hl7.fhir.r4.model.ConceptMap dom = (org.hl7.fhir.r4.model.ConceptMap) resourceR4;
                    resources.put(dom.getUrl(),dom);
                }
                //CapabilityStatement capabilityStatement = (CapabilityStatement)
            }
        } catch (Exception ex) {
                    log.error(ex.getMessage());
             }
         }

    public void populateMap() {
        getStructureDefinitions();
        getValueSets();
        getCodeSystems();
        getConceptMaps();
    }

    public Map<String, IBaseResource> getResources() {
        return resources;
    }

    public void setResources(Map<String, IBaseResource> resources) {
        this.resources = resources;
    }

    public void getStructureDefinitions() {

        Bundle bundle = null;
        try {
            bundle = client.search()
                    .forResource(StructureDefinition.class)
                    .returnBundle(Bundle.class)
                    .execute();
        } catch (Exception ex1) {
            log.error(ex1.getMessage());
        }

        if (bundle != null && bundle.hasEntry()) {
            Boolean more;
            do {
                more=false;
                for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                    log.info(entry.getFullUrl());
                    log.info(entry.getFullUrl());
                    if (entry.getResource() instanceof StructureDefinition) {
                        StructureDefinition sd = (StructureDefinition) entry.getResource();
                        sd.setSnapshot(null);
                        this.addMapEntry(sd.getUrl(),sd);
                    }
                }
                if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                    // load next page
                    log.info(bundle.getLink(Bundle.LINK_NEXT).getUrl());
                    try {
                        bundle = client.loadPage().next(bundle).execute();
                        more = true;
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                        more= false;
                    }
                }
            } while (more);
        }

    }
    public void getCodeSystems() {

        Bundle bundle = null;
        try {
            bundle = client.search()
                    .forResource(CodeSystem.class)
                    .returnBundle(Bundle.class)
                    .execute();
        } catch (Exception ex1) {
            log.error(ex1.getMessage());
        }

        if (bundle != null && bundle.hasEntry()) {
            Boolean more;
            do {
                more=false;
                for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                    log.info(entry.getFullUrl());
                    log.info(entry.getFullUrl());
                    if (entry.getResource() instanceof CodeSystem) {
                        CodeSystem cs = (CodeSystem) entry.getResource();
                        this.addMapEntry(cs.getUrl(),cs);
                    }
                }
                if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                    // load next page
                    log.info(bundle.getLink(Bundle.LINK_NEXT).getUrl());
                    try {
                        bundle = client.loadPage().next(bundle).execute();
                        more = true;
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                        more= false;
                    }
                }
            } while (more);
        }

    }
    public void getValueSets() {

        Bundle bundle = null;
        try {
            bundle = client.search()
                    .forResource(ValueSet.class)
                    .returnBundle(Bundle.class)
                    .execute();
        } catch (Exception ex1) {
            log.error(ex1.getMessage());
        }

        if (bundle != null && bundle.hasEntry()) {
            Boolean more;
            do {
                more=false;
                for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                    log.info(entry.getFullUrl());
                    if (entry.getResource() instanceof ValueSet) {
                        ValueSet vs = (ValueSet) entry.getResource();
                        this.addMapEntry(vs.getUrl(),vs);
                    }
                }
                if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                    // load next page
                    log.info(bundle.getLink(Bundle.LINK_NEXT).getUrl());
                    try {
                        bundle = client.loadPage().next(bundle).execute();
                        more = true;
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                        more= false;
                    }
                }
            } while (more);
        }
    }

    public void getConceptMaps() {

        Bundle bundle = null;
        try {
            bundle = client.search()
                    .forResource(ConceptMap.class)
                    .returnBundle(Bundle.class)
                    .execute();
        } catch (Exception ex1) {
            log.error(ex1.getMessage());
        }

        if (bundle != null && bundle.hasEntry()) {
            Boolean more;
            do {
                more=false;
                for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                    log.info(entry.getFullUrl());
                    if (entry.getResource() instanceof ConceptMap) {
                        ConceptMap cm = (ConceptMap) entry.getResource();
                        this.addMapEntry(cm.getUrl(),cm);
                    }
                }
                if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                    // load next page
                    log.info(bundle.getLink(Bundle.LINK_NEXT).getUrl());
                    try {
                        bundle = client.loadPage().next(bundle).execute();
                        more = true;
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                        more= false;
                    }
                }
            } while (more);
        }
    }

    public void getNamingSystems() {

        Bundle bundle = null;
        try {
            bundle = client.search()
                    .forResource(NamingSystem.class)
                    .returnBundle(Bundle.class)
                    .execute();
        } catch (Exception ex1) {
            log.error(ex1.getMessage());
        }

        if (bundle != null && bundle.hasEntry()) {
            Boolean more;
            do {
                more=false;
                for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                    log.info(entry.getFullUrl());
                    if (entry.getResource() instanceof NamingSystem) {
                        NamingSystem ns = (NamingSystem) entry.getResource();
                        this.addMapEntry(ns.getUrl(),ns);
                    }
                }
                if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                    // load next page
                    log.info(bundle.getLink(Bundle.LINK_NEXT).getUrl());
                    try {
                        bundle = client.loadPage().next(bundle).execute();
                        more = true;
                    } catch (Exception ex) {
                        log.error(ex.getMessage());
                        more= false;
                    }
                }
            } while (more);
        }
    }

    private void addMapEntry(String url, Resource resource) {
        String[] urlParse = url.split("/");
        String newUrl = urlParse[urlParse.length-1];
        newUrl = newUrl.replace("CareConnect-","").replace("Extension-","");
        if (this.resources.get(newUrl) != null) {
            if (resource instanceof CodeSystem) {
                newUrl = "cs-"+newUrl;
            }
            if (resource instanceof ValueSet) {
                newUrl = "vs-"+newUrl;
            }
            if (resource instanceof ConceptMap) {
                newUrl = "cm-"+newUrl;
            }
            if (resource instanceof NamingSystem) {
                newUrl = "ns-"+newUrl;
            }
        }

        this.resources.put(newUrl,resource);
    }

    private IBaseResource convertsToR4(IBaseResource resource) {
        org.hl7.fhir.dstu3.model.Resource resourceR3 = (Resource) resource;
        org.hl7.fhir.r4.model.Resource resourceR4 = convertor.convertResource(resourceR3,false);

        return resourceR4;

    }

}
