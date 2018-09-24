/*
 * Copyright 2018 Thomas Winkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rest;


import daos.machine.JPAMachineDao;
import daos.tool.JPAToolDao;
import daos.tool.JPAToolSettingDao;
import entities.Machine;
import entities.Tool;
import entities.ToolSetting;
import org.slf4j.Logger;
import utils.XLogger;
;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Path("/tool")
@RequestScoped
public class ToolSettingEndpoint {

    @Inject
    @XLogger
    Logger logger;
    @Inject
    private JPAToolSettingDao dao;
    @Inject
    private JPAToolDao toolDao;
    @Inject
    private JPAMachineDao machineDao;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tool> getAllTools() {
        return toolDao.readAllTools();
    }

    @GET
    @Path("/{toolId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Tool getTool(@PathParam("toolId") String toolId) {
        logger.debug("ToolSettingEndpoint.getTool");
        return toolDao.readToolForId(Integer.valueOf(toolId));
    }

    @GET
    @Path("/{toolName}/variants")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getToolVersions(@PathParam("toolName") String toolName) {
        logger.debug("ToolSettingEndpoint.getToolVersions");

        List<String> versions = new ArrayList<>();
        for (Tool t :toolDao.findVersionsOfTool(toolName)) {
            if(t.getVersion() != null)
                versions.add(t.getVersion());
        }
        return versions;
    }

    @GET
    @Path("/{toolName}/toolsettings")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ToolSetting> getToolSettings(@PathParam("toolName") String toolName) {
        logger.debug("ToolSettingEndpoint.getToolSettings");
        return dao.readToolSettingsForTool(toolName);
    }

    @GET
    @Path("/{machineName}/{toolName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ToolSetting> getToolSettingForToolAndMachine(@PathParam("machineName") String machineName, @PathParam("toolName") String toolName) {
        logger.debug("ToolSettingEndpoint.getToolSettingForToolAndMachine");
        logger.debug("machineName = [" + machineName + "], toolName = [" + toolName + "]");
        List<ToolSetting> y = dao.readToolSettingsForMachineAndTool(machineName, toolName);
        return y;
    }

    @GET
    @Path("/{machineName}/{toolName}/{version}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ToolSetting> getToolSettingForToolMachineAndVersion(@PathParam("machineName") String machineName, @PathParam("toolName") String toolName, @PathParam("version") String version) {
        logger.debug("ToolSettingEndpoint.getToolSettingForToolMachineAndVersion");
        List<ToolSetting> y = dao.readToolSettingsForMachineToolAndVersion(machineName, toolName, version);
        return y;
    }

    @GET
    @Path("/nomachine/{toolName}/{version}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ToolSetting> getToolSettingForToolAndVersion(@PathParam("toolName") String toolName, @PathParam("version") String version) {
        logger.debug("ToolSettingEndpoint.getToolSettingForToolAndVersion");
        logger.debug("toolName = [" + toolName + "], version = [" + version + "]");
        List<ToolSetting> y = dao.readToolSettingsForToolAndVersion(toolName, version);
        return y;
    }

    /*@POST
    @Path("/{machineName}/{toolName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setToolSettingForToolAndMachine(List<ToolSetting> l, @PathParam("machineName") String machineName, @PathParam("toolName") String toolName) {

        dao.writeToolSettings(l, machineName, toolName);
        return Response.status(200).entity("In Ordnung").build();
    }
    */

    @POST
    @Path("/{machineName}/{toolName}/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setToolSettingForToolMachineAndVersion(List<ToolSetting> l, @PathParam("machineName") String machineName, @PathParam("toolName") String toolName, @PathParam("version") String version) {

        logger.debug("ToolSettingEndpoint.setToolSettingForToolMachineAndVersion");

        dao.writeToolSettings(l, machineName, toolName, version);
        return Response.status(200).entity("In Ordnung").build();
    }

    @GET
    @Path("/machinesForTool/{toolName}/{version}/{machineId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Machine> getMachineaForTool(@PathParam("toolName") String toolName, @PathParam("version") String version,  @PathParam("machineId") Long machineId) {
        logger.debug("ToolSettingEndpoint.getMachineaForTool");
        logger.debug("toolId = [" + toolName + "], version = [" + version + "], machineId = [" + machineId + "]");
        List<Machine> machines;
        if (version.equals("null")) {
            System.out.println("Version null");
            machines = dao.getMachinesForToolViaTS(toolName);
        } else
            machines = dao.getMachinesForToolViaTS(toolName, version);

        List<Machine> ms = new ArrayList<>(machines);

        System.out.println(ms.size());
        if(!machines.contains(dao.getMachine(machineId)))
            ms.add(dao.getMachine(machineId));
        return ms;

    }
}
