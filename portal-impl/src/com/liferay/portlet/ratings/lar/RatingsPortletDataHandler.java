/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.ratings.lar;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.exportimport.lar.BasePortletDataHandler;
import com.liferay.portlet.exportimport.lar.ExportImportProcessCallbackRegistryUtil;
import com.liferay.portlet.exportimport.lar.PortletDataContext;
import com.liferay.portlet.exportimport.lar.PortletDataHandlerBoolean;
import com.liferay.portlet.exportimport.lar.StagedModelDataHandlerUtil;
import com.liferay.portlet.exportimport.lar.StagedModelType;
import com.liferay.portlet.exportimport.xstream.XStreamAliasRegistryUtil;
import com.liferay.portlet.ratings.model.RatingsEntry;
import com.liferay.portlet.ratings.service.RatingsEntryLocalServiceUtil;

import java.util.List;
import java.util.concurrent.Callable;

import javax.portlet.PortletPreferences;

/**
 * @author Gergely Mathe
 */
public class RatingsPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "ratings";

	public RatingsPortletDataHandler() {
		setDataAlwaysStaged(true);
		setDeletionSystemEventStagedModelTypes(
			new StagedModelType(RatingsEntry.class));
		setExportControls(
			new PortletDataHandlerBoolean(
				NAMESPACE, "ratings-entries", true, false, null,
				RatingsEntry.class.getName()));
		setImportControls(getExportControls());
		setPublishToLiveByDefault(true);

		XStreamAliasRegistryUtil.register(RatingsEntry.class, "RatingsEntry");
	}

	@Override
	protected String doExportData(
			final PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		Element rootElement = addExportDataRootElement(portletDataContext);

		if (!portletDataContext.getBooleanParameter(
				NAMESPACE, "ratings-entries")) {

			return getExportDataRootElementString(rootElement);
		}

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery(portletDataContext);

		actionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences, String data)
		throws Exception {

		ExportImportProcessCallbackRegistryUtil.registerCallback(
			new ImportRatingsCallable(portletDataContext));

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			final PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		ActionableDynamicQuery actionableDynamicQuery =
			getActionableDynamicQuery(portletDataContext);

		actionableDynamicQuery.performCount();
	}

	protected ActionableDynamicQuery getActionableDynamicQuery(
			final PortletDataContext portletDataContext)
		throws Exception {

		ExportActionableDynamicQuery actionableDynamicQuery =
			RatingsEntryLocalServiceUtil.getExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.setStagedModelType(
			new StagedModelType(
				PortalUtil.getClassNameId(RatingsEntry.class.getName()),
				StagedModelType.REFERRER_CLASS_NAME_ID_ALL));

		return actionableDynamicQuery;
	}

	private class ImportRatingsCallable implements Callable<Void> {

		public ImportRatingsCallable(PortletDataContext portletDataContext) {
			_portletDataContext = portletDataContext;
		}

		@Override
		public Void call() throws PortalException {
			if (!_portletDataContext.getBooleanParameter(
					NAMESPACE, "ratings-entries")) {

				return null;
			}

			Element entriesElement =
				_portletDataContext.getImportDataGroupElement(
					RatingsEntry.class);

			List<Element> entryElements = entriesElement.elements();

			for (Element entryElement : entryElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					_portletDataContext, entryElement);
			}

			return null;
		}

		private final PortletDataContext _portletDataContext;

	}

}