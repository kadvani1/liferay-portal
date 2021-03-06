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

package com.liferay.site.navigation.breadcrumb.web.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.ViewPortletProvider;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.site.navigation.breadcrumb.web.constants.BreadcrumbPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	immediate = true,
	property = {
		"model.class.name=com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry"
	},
	service = ViewPortletProvider.class
)
public class BreadcrumbEntryViewPortletProvider
	extends BasePortletProvider implements ViewPortletProvider {

	@Override
	public String getPortletId() {
		return BreadcrumbPortletKeys.BREADCRUMB;
	}

	@Override
	protected long getPlid(ThemeDisplay themeDisplay) throws PortalException {
		return themeDisplay.getPlid();
	}

}