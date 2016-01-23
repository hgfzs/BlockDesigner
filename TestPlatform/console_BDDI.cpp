#include "console_BDDI.h"
#include "console.h"

BDDIRegInfo ast_ConsoleRegInfo[] = 
{
	{"hw_reg", 8, BDDIRegTypeUINT},
	{"w_reg", 16, BDDIRegTypeUINT},
	{"dw_reg", 32, BDDIRegTypeUINT},
	{"lw_reg", 64, BDDIRegTypeUINT},
	{"b_reg", 8, BDDIRegTypeBOOL},
	{"h_reg", 32, BDDIRegTypeHEX},
	{"f_reg", 32, BDDIRegTypeFLOAT},
	{"df_reg", 64, BDDIRegTypeFLOAT},
	{"a_reg", 8, BDDIRegTypeSTRING}
};

CONSOLE_BDDI::CONSOLE_BDDI(CONSOLE *c) : target(c)
{

}

CONSOLE_BDDI::~CONSOLE_BDDI()
{

}

BDDIReturn CONSOLE_BDDI::BDDIGetRegisterValues(unsigned int RegIndex, char *OutValue)
{
	BDDIRegValue st_Temp;

	UINT8 hw_Temp;
	UINT16 w_Temp;
	UINT32 dw_Temp;
	UINT64 lw_Temp;
	bool b_Temp;
	UINT32 h_Temp;
	float f_Temp;
	double df_Temp;
	char a_Temp[128];

	switch(RegIndex)
	{
		case 0:
			hw_Temp = target->hw_reg;
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], &hw_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		case 1:
			w_Temp = target->w_reg;
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], &w_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		case 2:
			dw_Temp = target->dw_reg;
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], &dw_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		case 3:
			lw_Temp = target->lw_reg;
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], &lw_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		case 4:
			b_Temp = target->b_reg;
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], &b_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		case 5:
			h_Temp = target->h_reg;
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], &h_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		case 6:
			f_Temp = target->f_reg;
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], &f_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		case 7:
			df_Temp = target->df_reg;
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], &df_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		case 8:
			strcpy(a_Temp, target->a_reg);
			BDDIPutInRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], a_Temp);
			BDDIConvertRegisterValueToString(&st_Temp, OutValue);

			break;

		default:	return BDDIStatusError;	

	}

	return BDDIStatusOK;
}

BDDIReturn CONSOLE_BDDI::BDDISetRegisterValues(unsigned int RegIndex, const char *SetValue)
{
	BDDIRegValue st_Temp;

	UINT8 hw_Temp;
	UINT16 w_Temp;
	UINT32 dw_Temp;
	UINT64 lw_Temp;
	bool b_Temp;
	UINT32 h_Temp;
	float f_Temp;
	double df_Temp;
	char a_Temp[128];

	switch(RegIndex)
	{
		case 0:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, &hw_Temp);
			target->hw_reg = hw_Temp;

			break;

		case 1:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, &w_Temp);
			target->w_reg = w_Temp;

			break;

		case 2:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, &dw_Temp);
			target->dw_reg = dw_Temp;

			break;

		case 3:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, &lw_Temp);
			target->lw_reg = lw_Temp;

			break;

		case 4:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, &b_Temp);
			target->b_reg = b_Temp;

			break;

		case 5:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, &h_Temp);
			target->h_reg = h_Temp;

			break;

		case 6:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, &f_Temp);
			target->f_reg = f_Temp;

			break;

		case 7:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, &df_Temp);
			target->df_reg = df_Temp;

			break;

		case 8:
			BDDIConvertStringToRegisterValue(&st_Temp, &ast_ConsoleRegInfo[RegIndex], SetValue);
			BDDIExtractRegisterValue(&st_Temp, a_Temp);
			strcpy(target->a_reg, a_Temp);

			break;

		default:	return BDDIStatusError;	

	}

	return BDDIStatusOK;
}