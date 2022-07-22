package com.ccc.search.controller;


import com.ccc.search.service.MallSearchService;
import com.ccc.search.vo.SearchParam;
import com.ccc.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Action;

@Controller
public class SearchController {

    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String getLimit(SearchParam param, Model model, HttpServletRequest request) {

        param.set_queryString(request.getQueryString());
        //根据页面传递过来的查询参数，去es中检索商品
        SearchResult result = mallSearchService.serach(param);
        model.addAttribute("result", result);

        return "list";
    }

}
