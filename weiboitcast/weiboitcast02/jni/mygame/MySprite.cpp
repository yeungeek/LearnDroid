#include "MySprite.h"
using namespace cocos2d;
CMySprite::CMySprite(void)
{
}

CMySprite::~CMySprite(void)
{
}
bool CMySprite::initWithImage(cocos2d::CCImage * pcImg, cocos2d::CCRect rect)
{
	assert(pcImg != NULL);
	CCTexture2D *pTexture = CCTextureCache::sharedTextureCache()->addUIImage(pcImg,"pic");
	if (pTexture)
	{
		return initWithTexture(pTexture, rect);
	}
	// don't release here.
	// when load texture failed, it's better to get a "transparent" sprite then a crashed program
	// this->release(); 
	return false;
}
