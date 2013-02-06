window.org_vaadin_googleanalytics_tracking_GoogleAnalyticsTracker = function() {
	var self = this;
	this.registerRpc({
		track : function(pageId) {
			var state = self.getState();
			var trackerId = state.trackerId;
			var domainName = state.domainName;

			try {
				var pageTracker = window._gat._getTracker(trackerId);
				if (domainName) {
					pageTracker._setDomainName(domainName);
				}
				if (pageId) {
					pageTracker._trackPageview(pageId);
				} else {
					pageTracker._trackPageview();
				}
			} catch (err) {
				if (console)
					console.log(err);
			}

		}
	});
}
