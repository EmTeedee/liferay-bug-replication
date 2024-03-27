/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.example.reserved.column.filter.demo.service.impl;

import com.liferay.portal.aop.AopService;

import java.util.List;

import com.example.reserved.column.filter.demo.model.Foo;
import com.example.reserved.column.filter.demo.service.base.FooServiceBaseImpl;
import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=foo", "json.web.service.context.path=Foo"
	},
	service = AopService.class
)
public class FooServiceImpl extends FooServiceBaseImpl {
	public List<Foo> getGroupFoo(long groupId) {
		return fooPersistence.filterFindByGroupId(groupId);
	}
}