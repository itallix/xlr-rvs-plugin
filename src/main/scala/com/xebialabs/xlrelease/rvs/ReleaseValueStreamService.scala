/**
  * Copyright (c) 2018. All rights reserved.
  *
  * This software and all trademarks, trade names, and logos included herein are the property of XebiaLabs, Inc. and its affiliates, subsidiaries, and licensors.
  */

package com.xebialabs.xlrelease.rvs

import java.io.InputStreamReader
import java.nio.charset.StandardCharsets.UTF_8
import java.sql.ResultSet
import java.util

import com.google.common.io.CharStreams
import com.xebialabs.deployit.repository.RepositoryAdapter
import com.xebialabs.deployit.security.{PermissionEnforcer, RoleService}
import com.xebialabs.xlplatform.utils.ResourceManagement
import com.xebialabs.xlrelease.api.internal.EffectiveSecurityDecorator.EFFECTIVE_SECURITY
import com.xebialabs.xlrelease.api.internal.InternalMetadataDecoratorService
import com.xebialabs.xlrelease.api.internal.ReleaseGlobalVariablesDecorator.GLOBAL_VARIABLES
import com.xebialabs.xlrelease.db.ArchivedReleases.{REPORT_RELEASES_END_DATE_COLUMN, REPORT_RELEASES_RELEASEJSON_COLUMN}
import com.xebialabs.xlrelease.db.sql.SqlBuilder.Dialect
import com.xebialabs.xlrelease.db.sql.archiving.SelectArchivedReleasesBuilder
import com.xebialabs.xlrelease.domain.Release
import com.xebialabs.xlrelease.domain.status.ReleaseStatus.COMPLETED
import com.xebialabs.xlrelease.reports.service.ReportParams
import com.xebialabs.xlrelease.service.ArchivingService.deserializeArchivedRelease
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._


@Service
class ReleaseValueStreamService @Autowired()(@Qualifier("reportingJdbcTemplate") jdbcTemplate: JdbcTemplate,
                                             repositoryAdapter: RepositoryAdapter,
                                             decoratorService: InternalMetadataDecoratorService,
                                             @Qualifier("reportingSqlDialect") implicit val reportingSqlDialect: Dialect,
                                             implicit val permissionEnforcer: PermissionEnforcer,
                                             implicit val roleService: RoleService) {

  def getCompletedReleases(reportParams: ReportParams): util.List[Release] = {

    val sqlQuery = new SelectArchivedReleasesBuilder(REPORT_RELEASES_RELEASEJSON_COLUMN)
      .withDates(reportParams.timeFrame, reportParams.from, reportParams.to)
      .withTags(reportParams.tags)
      .withSecurity(reportParams.userSpecific)
      .withNotNull(REPORT_RELEASES_END_DATE_COLUMN)
      .withOneOfStatuses(COMPLETED.value())
      .withFilters(reportParams.filters)
      .orderBy(s"$REPORT_RELEASES_END_DATE_COLUMN DESC")

    val (sql, params) = sqlQuery.build()

    val releases = jdbcTemplate.query(sql, params.toArray, (rs: ResultSet, _: Int) => {
      ResourceManagement.using(rs.getBinaryStream(REPORT_RELEASES_RELEASEJSON_COLUMN))(is =>
        CharStreams.toString(new InputStreamReader(is, UTF_8)))
    }).asScala
      .map(json => deserializeArchivedRelease(json, repositoryAdapter)).asJava

    decoratorService.decorate(releases, List(GLOBAL_VARIABLES, EFFECTIVE_SECURITY).asJava)

    releases
  }
}
