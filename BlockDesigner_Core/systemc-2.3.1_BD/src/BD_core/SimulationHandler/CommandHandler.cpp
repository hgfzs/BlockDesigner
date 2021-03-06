//-----------------------------------------------------------------------------
// Design								: Block Designer Command Queue 
// Author								: Bryan Choi 
// Email								: bryan.choi@twoblocktech.com 
// File		     					: CommandHandler.cpp
// Date	       					: 2016/1/4
// Reference            :
// ----------------------------------------------------------------------------
// Copyright (c) 2015-2016 TwoBlock Techinologies Co.
// ----------------------------------------------------------------------------
// Description	: This class provide CommandHandler API 
// ----------------------------------------------------------------------------

#include "CommandHandler.h"	

namespace BDapi
{	
	/*
	 * function    	: Constructor
	 * design	      : get all mananger instances
	 */
	CommandHandler::CommandHandler()
	{
		CmdExecutionManager = ExecutionManager::GetInstance();
		CmdBDDIManager = BDDIManager::GetInstance();
		CmdModuleListManager = ModuleListManager::GetInstance();
		CmdBDPMDInitManager = BDPMDInitManager::GetInstance();
		CmdPMMLGenerationManager = PMMLGenerationManager::GetInstance();
		CmdSignalTraceManager = SignalTraceManager::GetInstance();
		CmdSoftwareManager = SoftwareManager::GetInstance();
		CmdCallBackManager = CallBackManager::GetInstance();
	}

	/*
	 * function    	: SetCommand 
	 * design	      : set command
	 * caller		    : SimulationHandler 
	 */
	void CommandHandler::SetCommand(GUI_COMMAND Command)
	{
		st_GUICommand = Command;
	}	

	/*
	 * function    	: Execute 
	 * design	      : check Operation( PUT or GET ) 
	 * caller		    : SimulationHandler 
	 * callee       : PutOperation, GetOperation 
	 */
	int CommandHandler::Execute()
	{
		if(st_GUICommand.Operation == PUT){
			PutOperation();
		}
		else if(st_GUICommand.Operation	== GET){
			GetOperation();
		}
		else; // Exeception

		return 0;
	}

	/*
	 * function    	: PutOperation 
	 * design	      : check command and perform command
	 * description  : 
	 * caller		    : Execute 
	 * callee       : 
	 */
	int CommandHandler::PutOperation()
	{
		if(st_GUICommand.Command == SoftwareLoad){
			SetManagerForPutOperation(CmdSoftwareManager);
		}
		else if(st_GUICommand.Command == ExecutionControl){
			SetManagerForPutOperation(CmdExecutionManager);
		}
		else if(st_GUICommand.Command == PutDebugInterface){
			SetManagerForPutOperation(CmdBDDIManager);
		}
		else if(st_GUICommand.Command == WireTraceControl)	{
			SetManagerForPutOperation(CmdSignalTraceManager);
		}	
		else if(st_GUICommand.Command == RegisterCallBack)	{
			SetManagerForPutOperation(CmdCallBackManager);
		}	
		else if(st_GUICommand.Command == LoadModule){
			SetManagerForPutOperation(CmdPMMLGenerationManager);
		}
		else if(st_GUICommand.Command == BDPMDInit)	{
			SetManagerForPutOperation(CmdBDPMDInitManager);
		} 
		return 0;
	}

	/*
	 * function    	: GetOperation 
	 * design	      : check command and perform command
	 * description  : 
	 * caller		    : Execute 
	 * callee       : 
	 */
	int CommandHandler::GetOperation()
	{
		if(st_GUICommand.Command == ModuleInfo){
			SetManagerForGetOperation(CmdPMMLGenerationManager);
		}
		else if(st_GUICommand.Command == GetDebugInterface){
			SetManagerForGetOperation(CmdBDDIManager);
		}

		return 0;
	}

	/*
	 * function    	: SetManagerForPutOperation
	 * design	      : set manager and perform put operation
	 * description  : 
	 * caller		    : PutOperation
	 * callee       : 
	 */
	void CommandHandler::SetManagerForPutOperation(TopManagerBase *manager)
	{
		manager->PutOperationControl(st_GUICommand);	
	}

	/*
	 * function    	: SetManagerForGetOperation 
	 * design	      : set manager and perform get operation
	 * description  : 
	 * caller		    : GetOperation
	 * callee       : 
	 */
	void CommandHandler::SetManagerForGetOperation(TopManagerBase *manager)
	{
		manager->GetOperationControl(st_GUICommand);	
	}
}
