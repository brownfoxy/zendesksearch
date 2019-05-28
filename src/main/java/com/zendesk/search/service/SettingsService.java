package com.zendesk.search.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phanindra on 27/05/19.
 */

/**
 * Container for settings about how organizations, tickets, users are related to one another
 */
public class SettingsService {

    private Map<String, Map<String, String>> relatedEntitySettings = new HashMap<>();

    SettingsService() {
        Map<String, String> userRelated = new HashMap<>();
        userRelated.put("organizations", "organization_id;_id");
        userRelated.put("tickets", "_id;assignee_id,submitter_id");
        relatedEntitySettings.put("users", userRelated);

        Map<String, String> ticketsRelated = new HashMap<>();
        ticketsRelated.put("users", "assignee_id,submitter_id;_id");
        ticketsRelated.put("organizations", "organization_id;_id");
        relatedEntitySettings.put("tickets", ticketsRelated);

        Map<String, String> organizationRelated = new HashMap<>();
        organizationRelated.put("users", "_id;organization_id");
        organizationRelated.put("tickets", "_id;organization_id");
        relatedEntitySettings.put("organizations", organizationRelated);
    }

    public Map<String, Map<String, String>> getRelatedEntitySettings() {
        return relatedEntitySettings;
    }
    public void setRelatedEntitySettings(Map<String, Map<String, String>> relatedEntitySettings) {
        this.relatedEntitySettings = relatedEntitySettings;
    }

}
