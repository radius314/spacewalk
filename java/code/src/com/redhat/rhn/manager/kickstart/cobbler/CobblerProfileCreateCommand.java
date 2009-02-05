/**
 * Copyright (c) 2009 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation. 
 */
package com.redhat.rhn.manager.kickstart.cobbler;

import com.redhat.rhn.common.validator.ValidatorError;
import com.redhat.rhn.domain.kickstart.KickstartData;
import com.redhat.rhn.domain.kickstart.KickstartableTree;
import com.redhat.rhn.domain.user.User;

import org.apache.log4j.Logger;
import org.cobbler.CobblerConnection;
import org.cobbler.Distro;
import org.cobbler.Profile;

import java.util.HashMap;
import java.util.Map;

/**
 * KickstartCobblerCommand - class to contain logic to communicate with cobbler
 * @version $Rev$
 */
public class CobblerProfileCreateCommand extends CobblerProfileCommand {

    private static Logger log = Logger.getLogger(CobblerProfileCreateCommand.class);
    
    
    /**
     * Constructor
     * @param ksDataIn to sync
     * @param userIn - user wanting to sync with cobbler
     */
    public CobblerProfileCreateCommand(KickstartData ksDataIn, User userIn) {
        super(ksDataIn, userIn);
    }

    /**
     * Call this if you want to use the taskomatic_user.
     * 
     * Useful for automated non-user initiated syncs
     * @param ksDataIn to sync
     */
    public CobblerProfileCreateCommand(KickstartData ksDataIn) {
        super(ksDataIn);
    }



     /**
     * Save the Cobbler profile to cobbler.
     * @return ValidatorError if there was a problem
     */
    public ValidatorError store() {
        CobblerConnection con = getCobblerConnection();
        
        KickstartableTree tree = ksData.getTree();
        Distro distro =  getDistroForKickstart();
        
        if (distro == null) {
            return new ValidatorError("kickstart.cobbler.profile.invalidvirt");
        }
        
        Profile prof = Profile.create(con, CobblerCommand.makeCobblerName(this.ksData),
                distro);
        prof.setVirtType(ksData.getKickstartDefaults().getVirtualizationType().getLabel());
        
        Map<String, String> meta = new HashMap<String, String>();
        meta.put("org", ksData.getOrg().getId().toString());
        prof.setKsMeta(meta);
        prof.setVirtPath("/var/lib/xen/" + ksData.getLabel());
        prof.setVirtBridge("xenbr0");
        prof.save();
                
        updateCobblerFields(prof);

        invokeCobblerUpdate();
        Map cProfile = getProfileMap();
        ksData.setCobblerId((String)cProfile.get("uid"));
        return null;
    }

}
