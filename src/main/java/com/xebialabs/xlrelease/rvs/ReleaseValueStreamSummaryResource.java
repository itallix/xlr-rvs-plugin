/**
 * Copyright (c) 2018. All rights reserved.
 *
 * This software and all trademarks, trade names, and logos included herein are the property of XebiaLabs, Inc. and its affiliates, subsidiaries, and licensors.
 */

package com.xebialabs.xlrelease.rvs;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.xebialabs.xlrelease.domain.Release;
import com.xebialabs.xlrelease.reports.api.ReportForm;
import com.xebialabs.xlrelease.reports.service.ReportParams;
import com.xebialabs.xlrelease.search.ReleaseSearchResult;
import com.xebialabs.xlrelease.security.PermissionChecker;
import com.xebialabs.xlrelease.service.TaskAccessService;
import com.xebialabs.xlrelease.views.ReleaseSearchView;
import com.xebialabs.xlrelease.views.converters.ReleaseViewConverter;

import static com.xebialabs.xlrelease.security.XLReleasePermissions.VIEW_REPORTS;

@Path("/rvs")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Controller
public class ReleaseValueStreamSummaryResource {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseValueStreamSummaryResource.class);

    private final PermissionChecker permissions;
    private final ReleaseValueStreamService releaseValueStream;
    private final ReleaseViewConverter releaseViewConverter;
    private final TaskAccessService taskAccessService;

    public ReleaseValueStreamSummaryResource(
            @Autowired PermissionChecker permissions,
            @Autowired ReleaseValueStreamService releaseValueStream,
            @Autowired ReleaseViewConverter releaseViewConverter,
            @Autowired TaskAccessService taskAccessService) {
        this.permissions = permissions;
        this.releaseValueStream = releaseValueStream;
        this.releaseViewConverter = releaseViewConverter;
        this.taskAccessService = taskAccessService;
    }

    @POST
    @Path("search")
    public ReleaseSearchView getData(ReportForm reportForm) {
        checkPermission(reportForm);
        List<Release> releases = releaseValueStream.getCompletedReleases(ReportParams.apply(reportForm));
        ReleaseSearchResult searchResult = new ReleaseSearchResult();
        searchResult.addReleases(releases);

        logger.info("Found {} releases", releases.size());

        return releaseViewConverter.toSearchView(searchResult, taskAccessService.getAllowedTaskTypesForAuthenticatedUser(),
                new ArrayList<String>(), 10, new ArrayList<String>());
    }

    private Release createSummary(List<Release> releases) {
        Release prototype = releases.get(0);


        return prototype;
    }

    private void checkPermission(ReportForm reportForm) {
        if (reportForm.isUserSpecific()) {
            permissions.check(VIEW_REPORTS);
        }
    }

}
