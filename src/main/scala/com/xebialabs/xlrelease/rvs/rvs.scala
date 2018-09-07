/**
  * Copyright (c) 2018. All rights reserved.
  *
  * This software and all trademarks, trade names, and logos included herein are the property of XebiaLabs, Inc. and its affiliates, subsidiaries, and licensors.
  */

package com.xebialabs.xlrelease

import com.xebialabs.xlrelease.db.sql.archiving.BaseSelectArchivedBuilder
import com.xebialabs.xlrelease.reports.filters.ReportFilter
import com.xebialabs.xlrelease.reports.filters.impl.DateFilter

import scala.collection.JavaConverters._

package object rvs {

  implicit class BuilderExtensions[T <: BaseSelectArchivedBuilder[T]](val builder: T) extends AnyVal {
    def withFilters(filters: java.util.List[ReportFilter]): T = withFilters(filters.asScala)

    def withFilters(filters: Seq[ReportFilter]): T = {
      if (filters != null) {
        filters
          .filterNot(filter => filter.isInstanceOf[DateFilter]) // currently this is passed directly as arguments
          .foreach(_.visit(builder.asInstanceOf[BaseSelectArchivedBuilder[_]]))
      }
      builder
    }
  }
}
