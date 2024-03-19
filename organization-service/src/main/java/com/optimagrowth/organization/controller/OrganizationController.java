package com.optimagrowth.organization.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.service.OrganizationService;

@RestController
@RequestMapping(value="v1/organization")
public class OrganizationController {
    @Autowired
    private OrganizationService service;

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value="/{organizationId}",method = RequestMethod.GET)
    public ResponseEntity<Organization> getOrganization(@PathVariable("organizationId") String organizationId, HttpServletRequest req) {
        System.out.println(req.getHeader("Authorization"));
        return ResponseEntity.ok(service.findById(organizationId));
    }

    @RolesAllowed({"ROLE_ADMIN"})
    @RequestMapping(value="/{organizationId}",method = RequestMethod.PUT)
    public void updateOrganization( @PathVariable("organizationId") String id, @RequestBody Organization organization) {
        service.update(organization);
    }

    @RolesAllowed({"ROLE_ADMIN"})
    @PostMapping
    public ResponseEntity<Organization>  saveOrganization(@RequestBody Organization organization) {
        return ResponseEntity.ok(service.create(organization));
    }

    @RolesAllowed({"ROLE_ADMIN"})
    @RequestMapping(value="/{organizationId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrganization( @PathVariable("id") String id,  @RequestBody Organization organization) {
        service.delete(organization);
    }

}