/*
** list.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:09:49 2014 Maxime renaud
** Last update Sun Mar 16 16:40:46 2014 Maxime renaud
*/

# include	<stdlib.h>
# include	<string.h>
# include	"../../include/my_elf.h"

static char     *p_type[22][3] =
  {
    {".bss", "B", "b"},
    {".data", "D", "d"},
    {".fini", "T", "t"},
    {".init", "T", "t"},
    {".note", "R", "r"},
    {".rodata", "R", "r"},
    {".text","T", "t"},
    {".jcr", "D", "d"},
    {".eh_frame", "R", "r"},
    {".comment", "R", "r"},
    {".dynamic", "d", "d"},
    {".dynstr", "R", "r"},
    {".dynsym", "R", "r"},
    {".got", "D", "d"},
    {".hash", "U", "u"},
    {".interp", "D", "d"},
    {".line", "R", "r"},
    {".rel", "R", "r"},
    {".shstrtab", "R", "r"},
    {".symtab", "R", "r"},
    {".plt", "R", "r"},
    {".tors", "D", "d"}
  };

char			getype(Elf32_Sym *symbol, t_elf *elf, int ecx)
{
  Elf32_Shdr		*sect_tmp;
  char			*sect_str_tmp;

  sect_tmp = &elf->shdr[symbol->st_shndx];
  if (symbol->st_shndx == SHN_ABS)
    return ((ELF32_ST_BIND(symbol->st_info) == STB_GLOBAL) ? 'A' : 'a');
  else if (symbol->st_shndx == SHN_COMMON)
    return ((ELF32_ST_BIND(symbol->st_info) == STB_GLOBAL) ? 'C' : 'c');
  sect_str_tmp = elf->shstrtab + sect_tmp->sh_name;
  while (++ecx < 22 - 2)
    {
      if (ELF32_ST_BIND(symbol->st_info) == STB_WEAK)
        return ('w');
      else if (strncmp(sect_str_tmp,
		       p_type[ecx][0], strlen(p_type[ecx][0])) == 0)
	return ((ELF32_ST_BIND(symbol->st_info) == STB_GLOBAL) ?
                p_type[ecx][1][0] : p_type[ecx][2][0]);
    }
  if (strstr(sect_str_tmp, p_type[22 - 1][0]) != NULL)
    return ((ELF32_ST_BIND(symbol->st_info) == STB_GLOBAL) ? 'D' : 'd');
  else if (symbol->st_shndx == SHN_UNDEF)
    return ((ELF32_ST_BIND(symbol->st_info) == STB_GLOBAL) ? 'U' : 'u');
  return ('?');
}

char			getype64(Elf64_Sym *symbol, t_elf *elf, int ecx)
{
  Elf64_Shdr		*sect_tmp;
  char			*sect_str_tmp;

  sect_tmp = &elf->shdr_64[symbol->st_shndx];
  if (symbol->st_shndx == SHN_ABS)
    return ((ELF64_ST_BIND(symbol->st_info) == STB_GLOBAL) ? 'A' : 'a');
  else if (symbol->st_shndx == SHN_COMMON)
    return ((ELF64_ST_BIND(symbol->st_info) == STB_GLOBAL) ? 'C' : 'c');
  sect_str_tmp = elf->shstrtab + sect_tmp->sh_name;
  while (++ecx < 22 - 2)
    {
      if (ELF64_ST_BIND(symbol->st_info) == STB_WEAK)
        return ('w');
      else if (strncmp(sect_str_tmp,
		       p_type[ecx][0], strlen(p_type[ecx][0])) == 0)
	return ((ELF64_ST_BIND(symbol->st_info) == STB_GLOBAL) ?
                p_type[ecx][1][0] : p_type[ecx][2][0]);
    }
  if (strstr(sect_str_tmp, p_type[22 - 1][0]) != NULL)
    return ((ELF64_ST_BIND(symbol->st_info) == STB_GLOBAL) ? 'D' : 'd');
  else if (symbol->st_shndx == SHN_UNDEF)
    return ((ELF64_ST_BIND(symbol->st_info) == STB_GLOBAL) ? 'U' : 'u');
  return ('?');
}
