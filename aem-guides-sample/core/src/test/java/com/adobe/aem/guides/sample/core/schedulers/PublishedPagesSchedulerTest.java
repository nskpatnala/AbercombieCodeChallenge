package com.adobe.aem.guides.sample.core.schedulers;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PublishedPagesSchedulerTest {

    PublishedPagesScheduler publishedPagesScheduler = new PublishedPagesScheduler();
    ResourceResolver resourceResolver;
    Session session;
    QueryBuilder queryBuilder;
    ResourceResolverFactory resourceResolverFactory;
    Query query;
    SearchResult searchResult;
    Hit hit;
    Node node;

    @BeforeEach
    protected void setup(){
        resourceResolver = Mockito.mock(ResourceResolver.class);
        session = Mockito.mock(Session.class);
        queryBuilder = Mockito.mock(QueryBuilder.class);
        resourceResolverFactory = Mockito.mock(ResourceResolverFactory.class);
        query = Mockito.mock(Query.class);
        searchResult = Mockito.mock(SearchResult.class);
        hit = mock(Hit.class);
        node = mock(Node.class);
    }

    @Test
    void run() throws Exception {
        PublishedPagesScheduler.Config config = Mockito.mock(PublishedPagesScheduler.Config.class);
        when(config.rootPath()).thenReturn("root path");
        publishedPagesScheduler.setQueryBuilder(queryBuilder);
        publishedPagesScheduler.setResourceResolverFactory(resourceResolverFactory);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "sampleSubService");
        when(resourceResolverFactory.getServiceResourceResolver(param)).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        lenient().when(queryBuilder.createQuery(any(PredicateGroup.class), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        List<Hit> hitList = new ArrayList<>();
        hitList.add(hit);
        when(searchResult.getHits()).thenReturn(hitList);
        when(hitList.get(0).getNode()).thenReturn(node);
        when(node.hasProperty("processedDate")).thenReturn(false);
        publishedPagesScheduler.run();
        publishedPagesScheduler.activate(config);

    }
}