//-----------------------------------------------------------------------------
// Design								: Block Designer Platform Module List Initialization
// Author								: Harold Ko
// Email								: harold.ko@twoblocktech.com 
// File		     					: BDPMDInitManager.cpp
// Date	       					: 2016/2/4
// ----------------------------------------------------------------------------
// Copyright (c) 2015-2016 TwoBlock Techinologies Co.
// ----------------------------------------------------------------------------
// Description	: Extract the information from BDPMD json file to initialize
//								Block Designer environment
// ----------------------------------------------------------------------------

#include "BDPMDInitManager.h"
#include "ModuleConnector.h"
#include "../SimulationAPI/ChannelMap.h"

namespace BDapi
{	
	// declare static variable for linker 
	BDPMDInitManager* BDPMDInitManager::_BDPMDInitManager = NULL;
	// initialize mutex 
	pthread_mutex_t BDPMDInitManager::BDPMDInitManagerInstanceMutex = PTHREAD_MUTEX_INITIALIZER;  

	/*
	 * function    	: PutOperationControl
	 * design	      :
	 * caller		    : CommandHandler::SetManagerForPutOperation
	 */
	void BDPMDInitManager::PutOperationControl(GUI_COMMAND Command)
	{
		ConnectModules(Command.Argu1);
	}

	/*
	 * function    	: GetOperationControl
	 * design	      : 
	 * caller		    : CommandHandler::SetManagerForGetOperation
	 */
	void BDPMDInitManager::GetOperationControl(GUI_COMMAND Command)
	{

	}

	/*
	 * function			: ConnectModules
	 * design				: Create Channels and Connect Modules
	 * param				: FilePath - this parameter is the path of BDPMD json file 
	 * caller				: BDPMDInitManager::PutOperationControl
	 */
	void BDPMDInitManager::ConnectModules(const char *FilePath)
	{
		ChannelInfo st_ChannelInfo = {NULL, NULL, NULL};
		BindingInfo st_BindingInfo = {NULL, NULL, NULL};

		ParsingPlatformManagerInformation(FilePath);

		for(unsigned int dw_PIndex = 0; ; dw_PIndex++)	{
			if(ParsingChannelInformation(dw_PIndex, &st_ChannelInfo) == BDPMDReturnStatusError)	break;
			else	{
				p_ChannelMap->AddChannel(&st_ChannelInfo);

				ParsingOwnConnectionInformation(dw_PIndex, &st_BindingInfo);
				p_ModuleConnector->BindChannel(&st_BindingInfo);

				for(unsigned int dw_CIndex = 0; ; dw_CIndex++)	{
					if(ParsingConnectionInformation(dw_PIndex, dw_CIndex, &st_BindingInfo) == BDPMDReturnStatusError)	break;
					else	{
						p_ModuleConnector->BindChannel(&st_BindingInfo);
					}
				}
			}
		}
	}

	/*
	 * function			: ParsingPlatformManagerInformation
	 * design				: Parsing BDPMD Json File and Extract Json Root Variable
	 * param				: FilePath - this parameter is the path of BDPMD json file 
	 * caller				: BDPMDInitManager::ConnectModules
	 */
	BDPMDReturnStatus BDPMDInitManager::ParsingPlatformManagerInformation(const char *FilePath)
	{
		bool b_ParsingStatus = false;

		ifstream PMInfo(FilePath, ifstream::binary);
		if(PMInfo == NULL)	{
			printf("Json File Path Error\n");
			return BDPMDReturnStatusError;
		}

		b_ParsingStatus = InfoReader.parse(PMInfo, InfoRoot, false);

		if(!b_ParsingStatus)	{
			cout << InfoReader.getFormatedErrorMessages() << endl;
			return BDPMDReturnStatusError;
		}

		InfoChannel = InfoRoot["BDPMD"]["Channel_Info"];

		return BDPMDReturnStatusOk;
	}

	/*
	 * function			: ParsingOwnConnectionInformation
	 * design				: Parsing BDPMD Json File and Extract Binding Information
	 * param				: Index					- this parameter is the index of Channel_Info in json file
   *								BindingObject - this parameter is the information of module and channel 
	 * caller				: BDPMDInitManager::ConnectModules
	 */
	BDPMDReturnStatus BDPMDInitManager::ParsingOwnConnectionInformation(unsigned int Index, BindingInfo* BindingObject)
	{
		char *phw_TempBuf = NULL;

		if(Index == InfoChannel.size())	return BDPMDReturnStatusError;
		else	{
			strcpy(a_TokBuf, (char *)InfoChannel[Index]["name"].asCString());

			BindingObject->ChannelName = InfoChannel[Index]["name"].asCString();
			phw_TempBuf = strtok(a_TokBuf, "$");
			BindingObject->ModuleName = phw_TempBuf;
			phw_TempBuf = strtok(NULL, "$");
			BindingObject->ModulePortName = phw_TempBuf;

			return BDPMDReturnStatusOk;
		}
	}

	/*
	 * function			: ParsingChannelInformation
	 * design				: Parsing BDPMD Json File and Extract Channel Information
	 * param				: Index					- this parameter is the index of Channel_Info in json file
   *								BindingObject - this parameter is the information of channel 
	 * caller				: BDPMDInitManager::ConnectModules
	 */
	BDPMDReturnStatus BDPMDInitManager::ParsingChannelInformation(unsigned int Index, ChannelInfo* ChannelObject)
	{
		if(Index == InfoChannel.size())	return BDPMDReturnStatusError;
		else	{
			ChannelObject->ChannelType = InfoChannel[Index]["channel_type"].asCString();
			ChannelObject->ChannelName = InfoChannel[Index]["name"].asCString();
			ChannelObject->DataType = InfoChannel[Index]["data_type"].asCString();

			return BDPMDReturnStatusOk;
		}
	}

	/*
	 * function			: ParsingConnectionInformation
	 * design				: Parsing BDPMD Json File and Extract Binding Information
	 * param				: PIndex				- this parameter is the index of Channel_Info in json file
	 *								CIndex				- this parameter is the index of connection_info in json file
   *								BindingObject - this parameter is the information of channel 
	 * caller				: BDPMDInitManager::ConnectModules
	 */
	BDPMDReturnStatus BDPMDInitManager::ParsingConnectionInformation(unsigned int PIndex, unsigned int CIndex, BindingInfo* BindingObject)
	{
		if(CIndex == InfoChannel[PIndex]["connection_info"].size())	return BDPMDReturnStatusError;
		else	{
			BindingObject->ModuleName = InfoChannel[PIndex]["connection_info"][CIndex]["module_name"].asCString();
			BindingObject->ModulePortName = InfoChannel[PIndex]["connection_info"][CIndex]["port_name"].asCString();
			BindingObject->ChannelName = InfoChannel[PIndex]["name"].asCString();
		
			return BDPMDReturnStatusOk;
		}
	}

	/*
	 * function 	: GetInstance
	 * design	    : singleton design
	 */
	BDPMDInitManager* BDPMDInitManager::GetInstance()
	{
		// lock
		pthread_mutex_lock(&BDPMDInitManagerInstanceMutex); 

		if( _BDPMDInitManager == NULL ){
			_BDPMDInitManager = new BDPMDInitManager();
		}
		// unlock
		pthread_mutex_unlock(&BDPMDInitManagerInstanceMutex);

		return _BDPMDInitManager;
	}

	/*
	 * function 	: DeleteInstance 
	 * design	    : Delete BDPMDInitManager instance 
	 */
	void BDPMDInitManager::DeleteInstance()
	{	
		delete _BDPMDInitManager;
		_BDPMDInitManager = NULL;
	}

	/*
	 * function 	: BDPMDInitManager 
	 * design	    : Constructor 
	 */
	BDPMDInitManager::BDPMDInitManager()
	{
		p_ChannelMap = ChannelMap::GetInstance();
		p_ModuleConnector = ModuleConnector::GetInstance();
	}

	/*
	 * function 	: ~BDPMDInitManager 
	 * design	    : Destructor 
	 */
	BDPMDInitManager::~BDPMDInitManager()
	{
	}
}