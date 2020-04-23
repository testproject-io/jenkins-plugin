package io.testproject.plugins;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import io.testproject.constants.Constants;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;

public class PluginConfiguration extends AbstractDescribableImpl<PluginConfiguration> {

    @Extension
    public static final PluginConfigurationDescriptor DESCRIPTOR = new PluginConfigurationDescriptor();

    @DataBoundConstructor
    public PluginConfiguration() {
    }

    @Override
    public Descriptor<PluginConfiguration> getDescriptor() {
        return DESCRIPTOR;
    }

    public static final class PluginConfigurationDescriptor extends Descriptor<PluginConfiguration> {

        private String apiKey;
        private boolean verbose;

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public void setVerbose(boolean verbose) {
            this.verbose = verbose;
        }

        public PluginConfigurationDescriptor() {
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            this.apiKey = json.getString("apiKey");
            this.verbose = json.getBoolean("verbose");

            save();

            return super.configure(req, json);
        }

        public FormValidation doCheckApiKey(@QueryParameter String value) {

            if (value.isEmpty())
                return FormValidation.error("Api Key cannot be empty");

            return FormValidation.ok();
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return Constants.TP_PLUGIN_CONFIGURATION;
        }

        public String getApiKey() {
            return apiKey;
        }

        public boolean isVerbose() {
            return verbose;
        }
    }
}
