package com.adobe.aem.guides.sample.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Code Begin- Naveen Patnala
 */
@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="sling/servlet/default",
        methods= HttpConstants.METHOD_GET,
        selectors = "searchdetails")
public class SearchServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServlet.class);
    @Reference
    QueryBuilder queryBuilder;
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {
       String searchTerm = request.getRequestParameter("searchTerm").toString();
        try {
            Session session = request.getResourceResolver().adaptTo(Session.class);
            Map<String, Object> predicateMap = new HashMap<>();
            JsonArray jsonArray = new JsonArray();
            predicateMap.put("path", "/content/sample");
            predicateMap.put("type", "cq:Page");
            predicateMap.put("p.limit","-1");
            Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
            SearchResult searchResult = query.getResult();
            List<Hit> hitList = searchResult.getHits();
            for (Hit hit : hitList) {
                Resource pageResource = hit.getResource().getResourceResolver().getResource(hit.getPath());
                if (null != pageResource) {
                    Page page = pageResource.adaptTo(Page.class);
                    if (null != page){
                    if ( (page.getPageTitle() != null && page.getPageTitle().contains(searchTerm)) || (page.getDescription() != null && page.getDescription().contains(searchTerm))) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("title", page.getPageTitle());
                        jsonObject.addProperty("description", page.getDescription());
                        jsonObject.addProperty("lastModified", page.getLastModified().getTime().toString());
                        Resource imageResource = request.getResourceResolver().getResource
                                (page.getPath() + "/jcr:content/image");
                        if (imageResource != null) {
                            String src = imageResource.getValueMap().get("fileReference", String.class);
                            jsonObject.addProperty("image", src);
                        }
                        jsonArray.add(jsonObject);
                    }

                    }
                }
            }
            response.getWriter().write(jsonArray.toString());
        }catch (RepositoryException re) {
            LOGGER.error("Exception occured {}",re);

        }


    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }
}
//code end - Naveen Patnala
