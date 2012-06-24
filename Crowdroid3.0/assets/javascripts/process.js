/**
 * Preview
 * @param {Object} url
 */
function setUrl(url) {
	process.openUrl(url);
}


function getRetweetStatusDataForWebView(){
	return process01.getRetweetStatusDataForWebView();
}


/**
 * Preview
 */
function getImageDataForWebView() {
	return process.getImageDataForWebView();
}

/**
 * Status
 */
function getStatusDataForWebView() {
	return process01.getStatusDataForWebView();
}

/**
 * Status
 * @param {Object} name
 */
function userName(name) {
	process01.startUserSearchActivity(name);
}

/**
 * Status
 * @param {Object} tag
 */
function hashTag(tag) {
	process01.startKeywordSearchActivity(tag);
}

function getEmotionDataForWebView() {
	return Image.getEmotionDataForWebView();
}

function getImage(phrase) {
    Image.setEmotion(phrase);
}

function jumpToProifle(screenName) {
    process01.jumpToProifle(screenName);
}