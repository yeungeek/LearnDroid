
#ifndef _GAME_OVER_SCENE_H_
#define _GAME_OVER_SCENE_H_

#include "cocos2d.h"
class CMySprite :
	public cocos2d::CCSprite
{
public:
	CMySprite(void);
	~CMySprite(void);
public:
	bool initWithImage(cocos2d::CCImage * pcImg, cocos2d::CCRect rect);

};

#endif // _GAME_OVER_SCENE_H_