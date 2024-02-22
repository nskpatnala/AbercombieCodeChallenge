package com.adobe.aem.guides.sample.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.jcr.Session;
import javax.jcr.Value;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ModifierDetailsServletTest {

    ModifierDetailsServlet modifierDetailsServlet = new ModifierDetailsServlet();

    QueryBuilder queryBuilder;
    SlingHttpServletRequest request;
    SlingHttpServletResponse response;
    Query query;
    SearchResult searchResult;
    Hit hit;
    Session session;
    RequestParameter requestParameter;
    ResourceResolver resourceResolver;
    Resource resource ;
    Page page;
    PrintWriter printWriter;
    ValueMap valueMap;
    Authorizable authorizable;
    UserManager userManager;
    Value value;

    @BeforeEach
    void setup() {
        queryBuilder = mock(QueryBuilder.class);
        request = mock(SlingHttpServletRequest.class);
        response = mock(SlingHttpServletResponse.class);
        session = Mockito.mock(Session.class);
        queryBuilder = Mockito.mock(QueryBuilder.class);
        query = Mockito.mock(Query.class);
        searchResult = Mockito.mock(SearchResult.class);
        hit = mock(Hit.class);
        requestParameter = mock(RequestParameter.class);
        resourceResolver = mock(ResourceResolver.class);
        resource = mock(Resource.class);
        page =mock(Page.class);
        printWriter = mock(PrintWriter.class);
        valueMap = mock(ValueMap.class);
        authorizable = mock(Authorizable.class);
        userManager = mock(UserManager.class);
        value = mock(Value.class);
    }

    @Test
    void doGet() throws Exception {
        modifierDetailsServlet.setQueryBuilder(queryBuilder);
        when(request.getResource()).thenReturn(resource);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        lenient().when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        List<Hit> hitList = new ArrayList<>();
        hitList.add(hit);
        when(searchResult.getHits()).thenReturn(hitList);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource(anyString())).thenReturn(resource);
        when(resource.getPath()).thenReturn("/content/sample");
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:lastModifiedBy",String.class)).thenReturn("admin");
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable("admin")).thenReturn(authorizable);
        when(hitList.get(0).getResource()).thenReturn(resource);
        when(resource.getParent()).thenReturn(resource);
        when(resource.getPath()).thenReturn("/content/currentpage");
        Value[] values = new Value[2];
        values[0]=value;
        when(authorizable.getProperty(anyString())).thenReturn(values);
        when(request.getPathInfo()).thenReturn("json");
        when(response.getWriter()).thenReturn(printWriter);
        modifierDetailsServlet.doGet(request,response);
    }
}