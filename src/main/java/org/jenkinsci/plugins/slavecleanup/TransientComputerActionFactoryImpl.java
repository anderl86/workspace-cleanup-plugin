/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jenkinsci.plugins.slavecleanup;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Computer;
import hudson.model.TransientComputerActionFactory;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author andi
 */
@Extension
public class TransientComputerActionFactoryImpl extends TransientComputerActionFactory {

    @Override
    public Collection<? extends Action> createFor(Computer cmptr) {
        List<Action> actions = new LinkedList<Action>();
        actions.add(new WorkspaceCleanupAction(cmptr));
        return actions;
    }

    
}
