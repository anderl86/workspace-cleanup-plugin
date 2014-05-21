/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jenkinsci.plugins.slavecleanup;

import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Computer;
import hudson.model.Item;
import hudson.model.Slave;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import org.kohsuke.stapler.StaplerFallback;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author andi
 */
public class WorkspaceCleanupAction implements Action, StaplerFallback {
    
    private final Computer computer;

    public WorkspaceCleanupAction(Computer computer) {
        this.computer = computer;
    }
    
    public Computer getComputer() {
        return computer;
    }

    public String getIconFileName() {
        return computer.hasPermission(Computer.CONFIGURE) ? "folder-delete.png" : null;
    }

    public String getDisplayName() {
        return computer.hasPermission(Computer.CONFIGURE) ? Messages.WorkspaceCleanupAction_name() : null;
    }

    public String getUrlName() {
        return computer.hasPermission(Computer.CONFIGURE) ? "workspaceCleanup" : null;
    }
    
    public Object getStaplerFallback() {
        return computer;
    }
    
    public void doDeleteWorkspace(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException, InterruptedException {
        if(computer.hasPermission(Computer.CONFIGURE)) {
            String workspace = req.getParameter("workspace");
            Slave slave = (Slave)computer.getNode();
            if(workspace != null && slave != null) {
                slave.getWorkspaceRoot().child(workspace).delete();
            }
            rsp.sendRedirect2(".");
        }
    }
    
    public List<Workspace> getWorkspaces() throws IOException, InterruptedException {
        List<Workspace> workspaces = new LinkedList<Workspace>();
              
        Map<String,AbstractProject> jobs = new HashMap<String,AbstractProject>();
        for(Item it: hudson.model.Hudson.getInstance().getItems()) {
            if(it instanceof AbstractProject)
                jobs.put(it.getName(), (AbstractProject)it);
        }
        
        for(FilePath dir: ((Slave)computer.getNode()).getWorkspaceRoot().listDirectories()) {
            Workspace ws = new Workspace();
            ws.name = dir.getName();
            if(jobs.containsKey(ws.name)) {
                ws.hasProject = true;
                ws.project = jobs.get(ws.name);
                ws.isBoundToNode = computer.getNode() != null && ws.project.getAssignedLabel() != null && ws.project.getAssignedLabel().matches(computer.getNode());
                ws.isBuilding = ws.project.isBuilding();
            }
            else {
                ws.hasProject = false;
                ws.isBoundToNode = false;
                ws.isBuilding = false;
            }
            workspaces.add(ws);
        }
        
        return workspaces;
    }


    
    public final class Workspace {
        public String name;
        public boolean hasProject;
        public AbstractProject project;
        public boolean isBoundToNode;
        public boolean isBuilding;
    }
    
}
