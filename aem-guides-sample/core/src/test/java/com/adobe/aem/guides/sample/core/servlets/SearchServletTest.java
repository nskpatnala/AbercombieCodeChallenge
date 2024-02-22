package com.adobe.aem.guides.sample.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.jcr.Session;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Code STart - Naveen patnala
 */
class SearchServletTest {

    SearchServlet searchServlet = new SearchServlet();
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
    Calendar calendar;
    Date date;

    @BeforeEach
    void setUp() {
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
        calendar = mock(Calendar.class);
        date= mock(Date.class);
        printWriter = mock(PrintWriter.class);
    }

    @Test
    void doGet() throws Exception {
        searchServlet.setQueryBuilder(queryBuilder);
        when(request.getRequestParameter("searchTerm")).thenReturn(requestParameter);
        when(requestParameter.toString()).thenReturn("samplestring");
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        lenient().when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        List<Hit> hitList = new ArrayList<>();
        hitList.add(hit);
        when(searchResult.getHits()).thenReturn(hitList);
        Mockito.when(hitList.get(0).getResource()).thenReturn(resource);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(hitList.get(0).getPath()).thenReturn("/content/sample");
        when(resourceResolver.getResource("/content/sample")).thenReturn(resource);
        when(resource.adaptTo(Page.class)).thenReturn(page);
        when(response.getWriter()).thenReturn(printWriter);
        when(page.getPageTitle()).thenReturn("samplestring");
        when(page.getDescription()).thenReturn("samplestring");
        lenient().when(page.getLastModified()).thenReturn(calendar);
        searchServlet.doGet(request,response);
    }
}
//code End - Naveen Patnala