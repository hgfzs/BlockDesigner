//-----------------------------------------------------------------------------
// Design								: Channel Map to manager channels 
// Author								: Bryan Choi 
// Email								: bryan.choi@twoblocktech.com 
// File		     					: ChannelMap.h
// Date	       					: 2016/1/26
// Reference            :
// ----------------------------------------------------------------------------
// Copyright (c) 2015-2016 TwoBlock Techinologies Co.
// ----------------------------------------------------------------------------
// Description	: manage channels and give  channel information to other manager 
// ----------------------------------------------------------------------------

#ifndef __CHANNEL_MAP_H__
#define __CHANNEL_MAP_H__

#include "systemc.h"
#include "string.h"
#include <map>
#include <pthread.h>

/*
 * namespace  	: BDapi 
 * design	      : Block Designer API 
 * description	: support analyzing ESL platform based on systemc
 */
namespace BDapi
{
	// this struct support map key( char * )
	// this protect comparing key based on address( char * ) 
	struct CmpStr 
	{
		bool operator()(char const *a, char const *b)
		{
			return std::strcmp(a, b) < 0;
		}
	};

	// ChannelInfo from Platform Manager
	struct ChannelInfo
	{
		const char *ChannelType;
		const char *ChannelName;
		const char *DataType;
	};

	// ChannelObject for Simulation 
	struct ChannelObject 
	{
	  char ChannelType[128];
		char DataType[128];
	  sc_interface *p_SCinterface;
	};

	/*
	 * class		    : 
	 * design	      :
	 * description	: 
	 */
	class ChannelMap
	{
		public:
			void AddChannel(ChannelInfo *st_ChannelInfo);
			void DeleteChannels();
			ChannelObject* FindChannel(const char *ChannelName);

			static ChannelMap* GetInstance();
			static void DeleteInstance();

		protected:
			ChannelMap();

		private:
			void AddSCsignal(const char *ChannelName, const char *DataType);
			void AddSCclock(const char *ChannelName, const char *DataType);

			std::map<const char*, ChannelObject*, CmpStr> RealChannelMap;
			std::map<const char*, ChannelObject*, CmpStr>::iterator ChannelFinder; 

			static ChannelMap *_ChannelMap; 
			// mutex for singleton pattern 
			static pthread_mutex_t ChannelMapInstanceMutex;   
	};
} // namespace BDapi 

#endif 
