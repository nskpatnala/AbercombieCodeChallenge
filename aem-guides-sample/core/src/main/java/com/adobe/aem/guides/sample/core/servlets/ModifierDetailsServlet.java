package com.adobe.aem.guides.sample.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.google.gson.JsonObject;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Code Starts - Naveen
 */
@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="sling/servlet/default",
        methods= HttpConstants.METHOD_GET,
        selectors = "modifierdetails",
        extensions={"json","xml"})
@ServiceDescription("")
public class ModifierDetailsServlet extends SlingSafeMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifierDetailsServlet.class);
    public static final String ROOT = "root";
    @Reference
    QueryBuilder queryBuilder;
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
    throws IOException {
        try {
            JsonObject jsonObject = new JsonObject();
            ResourceResolver resourceResolver = request.getResourceResolver();
            Session session = resourceResolver.adaptTo(Session.class);
            Resource pageResource = request.getResource();
            Resource pageContentResource = pageResource.getResourceResolver().getResource(pageResource.getPath() + "/jcr:content");
            if (null!= pageContentResource) {
                String userId = pageContentResource.getValueMap().get("cq:lastModifiedBy", String.class);
                UserManager userManager = resourceResolver.adaptTo(UserManager.class);
                if (null != userManager) {
                    Authorizable authorizable = userManager.getAuthorizable(userId);
                    Value[] lastName = authorizable.getProperty("./profile/familyName");
                    Value[] firstName = authorizable.getProperty("./profile/givenName");
                    Map<String, Object> predicateMap = new HashMap<>();
                    predicateMap.put("path",pageResource.getPath());
                    predicateMap.put("type","cq:PageContent");
                    predicateMap.put("property","cq:lastModifiedBy");
                    predicateMap.put("property.value",userId);
                    predicateMap.put("p.limit","-1");
                    Query query = queryBuilder.createQuery(PredicateGroup.create(predicateMap),session);
                    SearchResult searchResult = query.getResult();
                    List<Hit> hitList = searchResult.getHits();
                    List<String> pageUrls = new ArrayList<>();
                    for (Hit hit : hitList){
                        if(!(hit.getResource().getParent().getPath().equals(request.getResource().getPath()))) {
                            pageUrls.add(hit.getResource().getParent().getPath());
                        }
                    }
                    jsonObject.addProperty("firstName",firstName[0].getString());
                    jsonObject.addProperty("lastName",lastName[0].getString());
                    jsonObject.addProperty("page urls",pageUrls.toString());
                }
            }
            if (request.getPathInfo().contains("json")) {
                response.setContentType("application/json");
                response.getWriter().write(jsonObject.toString());
            } else if (request.getPathInfo().contains("xml")) {
                JSONObject object = new JSONObject(jsonObject.toString());
                String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<"+ROOT+">" + XML.toString(jsonObject) + "</"+ROOT+">";
                response.getWriter().write(xml);
            }
        } catch (RepositoryException | JSONException re) {
            LOGGER.error("Exception");
        }


    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }
}
/**
 * Code Ends - Naveen
 */
