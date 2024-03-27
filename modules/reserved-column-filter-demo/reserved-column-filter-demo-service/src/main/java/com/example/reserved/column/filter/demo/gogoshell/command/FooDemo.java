package com.example.reserved.column.filter.demo.gogoshell.command;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.PortalUtil;

import com.example.reserved.column.filter.demo.service.FooLocalService;
import com.example.reserved.column.filter.demo.service.FooService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    property = {
        "osgi.command.function=demo",
        "osgi.command.scope=foo"
    },
    service = Object.class
)
public class FooDemo {

    public String demo() {
        StringBundler sb = new StringBundler();

        try {
            Company company = _companyService.getCompanyById(
                PortalUtil.getDefaultCompanyId());

            sb.append("Try as Admin User\n");
            setPermissionChecker(company, true);
            sb.append(runTest(company));

            sb.append("Try as non-Admin User\n");
            setPermissionChecker(company, false);
            sb.append(runTest(company));

        } catch (Exception exception) {
            sb.append("Caught Exception: ")
                .append(getRootCause(exception))
                .append("\n");
        }

        return sb.toString();
    }

    public StringBundler runTest(Company company) {
        StringBundler sb = new StringBundler();

        sb.append("  - Test findByGroupId\n");
        try {
            _fooLocalService.getGroupFoo(company.getGroupId());
            sb.append("    -> OK\n");
        } catch (Exception exception) {
            sb.append("    -> Error: ").append(getRootCause(exception)).append("\n");;
            _log.error("Exception in findByGroupId", exception);
        }

        sb.append("  - Test filterFindByGroupId\n");
        try {
            _fooService.getGroupFoo(company.getGroupId());
            sb.append("    -> OK\n");
        } catch (Exception exception) {
            sb.append("    -> Error: ").append(getRootCause(exception)).append("\n");;
            _log.error("Exception in filterFindByGroupId", exception);
        }

        return sb;
    }

    /**
     * This sets the thread permission checker
     * @param company
     * @param isAdmin
     * @return
     * @throws PortalException
     */
    private void setPermissionChecker(Company company, boolean isAdmin) throws PortalException {
        Role role = _roleLocalService.getRole(
            company.getCompanyId(), RoleConstants.ADMINISTRATOR);

        for (User user : _userLocalService.getCompanyUsers(company.getCompanyId(),
            QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {
            if (_roleLocalService.hasUserRole(user.getUserId(), role.getRoleId()) == isAdmin) {
                PermissionChecker checker = PermissionCheckerFactoryUtil.create(user);
                PermissionThreadLocal.setPermissionChecker(checker);
                return;
            }
        }
        throw new PortalException("Could not find " + (isAdmin ? "admin" : "non-admin") + " user.");
    }

    public static Throwable getRootCause(final Throwable throwable) {
        final Throwable cause = throwable.getCause();
        return (cause != null) ? getRootCause(cause) : throwable;
    }

    @Reference
    protected CompanyService _companyService;

    @Reference
    protected FooLocalService _fooLocalService;

    @Reference
    protected FooService _fooService;

    @Reference
    protected UserLocalService _userLocalService;

    @Reference
    protected RoleLocalService _roleLocalService;

    private static final Log _log = LogFactoryUtil.getLog(FooDemo.class);
}
