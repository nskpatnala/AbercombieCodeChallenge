package com.adobe.aem.guides.sample.core.schedulers;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
*** Begin Code - Naveen Patnala
 */
@Component(service = Runnable.class)
@Designate(ocd = PublishedPagesScheduler.Config.class)
public class PublishedPagesScheduler implements Runnable {

    @Reference
    ResourceResolverFactory resourceResolverFactory;
    @Reference
    QueryBuilder queryBuilder;
    @ObjectClassDefinition(name="Service For Published Page " ,
            description = "Identify published pages and set a property for current date")
    public static @interface Config {

        @AttributeDefinition(name = "Cron-job expression")
        String scheduler_expression() default "";

        @AttributeDefinition(name = "Root Content Path",
                description = "Root Content Path for Searching Published Pages")
        String rootPath() default "/content/sample";
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String rootPath;

    @Override
    public void run() {
        Session session = null;
        try {
            Map<String, Object> predicateMap = new HashMap<>();
            predicateMap.put("path", rootPath);
            predicateMap.put("1_type", "cq:PageContent");
            predicateMap.put("1_property", "cq:lastReplicationAction");
            predicateMap.put("1_property.operation", "exists");
            predicateMap.put("2_property","cq:lastReplicationAction");
            predicateMap.put("2_property.value","Activate");
            predicateMap.put("p.limit","-1");
            session = getResourceResolver().adaptTo(Session.class);
            if (null != session) {
            Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap),session);
            SearchResult searchResult = query.getResult();
            if (null != searchResult) {
                List<Hit> hitList = searchResult.getHits();
                for(Hit hit : hitList) {
                    Node pageContentResource = hit.getNode();
                    if (!pageContentResource.hasProperty("processedDate")) {
                        Calendar calendar = Calendar.getInstance();
                        pageContentResource.setProperty("processedDate", calendar);
                        session.save();
                    }
                }

                }
            }
        } catch (RepositoryException | LoginException e) {
            logger.error("Exception occured {}",e);
        }finally {
            if (null != session && session.isLive()) {
                session.logout();
            }
        }

    }

    @Activate
    protected void activate(final PublishedPagesScheduler.Config config) {
        rootPath = config.rootPath();
    }
    private ResourceResolver getResourceResolver() throws LoginException {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "sampleSubService");
        ResourceResolver resolver = null;
        resolver = resourceResolverFactory.getServiceResourceResolver(param);
        return resolver;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public void setResourceResolverFactory(ResourceResolverFactory resourceResolverFactory) {
        this.resourceResolverFactory = resourceResolverFactory;
    }
}
//End Code - Naveen Patnala