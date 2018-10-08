/**
  * Copyright (c) 2018. All rights reserved.
  *
  * This software and all trademarks, trade names, and logos included herein are the property of XebiaLabs, Inc. and its affiliates, subsidiaries, and licensors.
  */

package com.xebialabs.xlrelease.rvs

import java.util

import com.xebialabs.xlrelease.reports.api.ReportForm
import com.xebialabs.xlrelease.reports.service.ReportParams
import com.xebialabs.xlrelease.search.ReleaseSearchResult
import com.xebialabs.xlrelease.security.XLReleasePermissions.VIEW_REPORTS
import com.xebialabs.xlrelease.security.{PermissionChecker, XLReleasePermissions}
import com.xebialabs.xlrelease.service.TaskAccessService
import com.xebialabs.xlrelease.views.ReleaseSearchView
import com.xebialabs.xlrelease.views.converters.ReleaseViewConverter
import javax.ws.rs.core.MediaType
import javax.ws.rs.{Consumes, POST, Path, Produces}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Path("/rvs-scala")
@Consumes(Array(MediaType.APPLICATION_JSON))
@Produces(Array(MediaType.APPLICATION_JSON))
@Controller
class ReleaseValueStreamResource @Autowired()(val permissions: PermissionChecker,
                                              val releaseValueStream: ReleaseValueStreamService,
                                              val releaseViewConverter: ReleaseViewConverter,
                                              val taskAccessService: TaskAccessService) {

  @POST
  @Path("search") def getData(reportForm: ReportForm): ReleaseSearchView = {
    checkPermission(reportForm)
    val releases = releaseValueStream.getCompletedReleases(ReportParams.apply(reportForm))
    val searchResult = new ReleaseSearchResult
    searchResult.addReleases(releases)
    releaseViewConverter.toSearchView(searchResult, taskAccessService.getAllowedTaskTypesForAuthenticatedUser,
      new util.ArrayList[String], 10, new util.ArrayList[String])
  }

  private def checkPermission(reportForm: ReportForm): Unit = {
    if (reportForm.isUserSpecific) permissions.check(VIEW_REPORTS)
  }
}
