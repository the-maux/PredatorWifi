/*
** main.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:10:47 2014 Maxime renaud
** Last update Sun Mar 16 20:24:32 2014 Maxime renaud
*/

# include		<stdio.h>
# include		<stdlib.h>
# include		<string.h>
# include		"../../include/my_elf.h"

extern	void		arg_s(t_elf *elf)
{
  int			ecx;
  Elf32_Shdr		*edi;
  unsigned int		addr;

  edi = elf->shdr;
  ecx = -1;
  if (elf->ehdr->e_shnum < 100)
    while (++ecx < elf->ehdr->e_shnum)
      {
	if (strcmp(elf->shstrtab + edi->sh_name, ".shstrtab") &&
	    strcmp(elf->shstrtab + edi->sh_name, ".strtab") &&
	    strcmp(elf->shstrtab + edi->sh_name, ".symtab") &&
	    strcmp(elf->shstrtab + edi->sh_name, ".bss") &&
	    strcmp(elf->shstrtab + edi->sh_name, ""))
	  {
	    if (edi->sh_size)
	      printf("Contents of section %s:\n", elf->shstrtab + edi->sh_name);
	    addr = edi->sh_size;
	    print_inside(edi, elf, NULL, addr);
	  }
	++edi;
      }
  return ;
}

extern	void		arg_s64(t_elf *elf)
{
  int			ecx;
  unsigned int		addr;
  Elf64_Shdr		*edi;

  edi = elf->shdr_64;
  ecx = -1;
  if (elf->ehdr_64->e_shnum < 100)
    while (++ecx < elf->ehdr_64->e_shnum)
      {
	if (strcmp(elf->shstrtab + edi->sh_name, ".shstrtab") &&
	    strcmp(elf->shstrtab + edi->sh_name, ".strtab") &&
	    strcmp(elf->shstrtab + edi->sh_name, ".symtab") &&
	    strcmp(elf->shstrtab + edi->sh_name, ".bss") &&
	    strcmp(elf->shstrtab + edi->sh_name, ""))
	  {
	    if (edi->sh_size)
	      printf("Contents of section %s:\n",
		     elf->shstrtab + edi->sh_name);
	    addr = edi->sh_size;
	    print_inside(NULL, elf, edi, addr);
	  }
	++edi;
      }
  return ;
}
