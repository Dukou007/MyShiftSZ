package com.pax.tms.user.web.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pax.tms.user.service.AppKeyService;

@RestController
@RequestMapping("/client")
public class AppKeyController {

	@Autowired
	private AppKeyService appKeyService;
	@RequiresPermissions(value = "tms:app client:get")
	@GetMapping("/get-app-key")
	public String getAppKey(@RequestParam("userName") String userName) {

		return appKeyService.getAppKey(userName);

	}
	
	@RequiresPermissions(value = "tms:app client:refresh")
	@PostMapping("/refresh-app-key")
	public String refreshAppKey(@RequestParam("userName") String userName) {

		return appKeyService.saveOrUpdateAppKey(userName);
	}
	@RequiresPermissions(value = "tms:app client:remove")
	@PostMapping("/remove-app-key")
	public void removeAppKey(@RequestParam("userName") String userName) {

		appKeyService.removeAppKey(userName);
	}

}
