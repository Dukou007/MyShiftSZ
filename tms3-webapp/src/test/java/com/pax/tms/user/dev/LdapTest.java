package com.pax.tms.user.dev;

import org.junit.Test;
import org.ldaptive.LdapException;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.user.security.LdapSearchExecutor;
import com.pax.tms.user.web.form.UserForm;

public class LdapTest extends ServiceJunitCase {

	@Autowired
	private LdapSearchExecutor ldapSearchExecutor;

	@Test
	public void testSearchUser() {
		try {
			UserForm form = ldapSearchExecutor.getUserInfo("wcx");
			System.out.println(form);
		} catch (LdapException e) {
			e.printStackTrace();
		}
	}

}
