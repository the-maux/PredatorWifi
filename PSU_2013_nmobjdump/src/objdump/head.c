/*
** head.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Wed Mar 12 15:34:00 2014 Maxime renaud
** Last update Sun Mar 16 20:21:41 2014 Maxime renaud
*/

# include	<stdio.h>
# include	<stdlib.h>
# include	<string.h>
# include	<unistd.h>
# include	"../../include/my_elf.h"

static	void	print_flag(int flag)
{
  char		*file_type[9];

  file_type[0] = "HAS_RELOC";
  file_type[1] = "EXEC_P";
  file_type[2] = "HAS_LINENO";
  file_type[3] = "HAS_DEBUG";
  file_type[4] = "HAS_SYMS";
  file_type[5] = "HAS_LOCALS";
  file_type[6] = "DYAMIC";
  file_type[7] = "WP_TEXT";
  file_type[8] = "D_PAGED";
  file_type[9] = NULL;
  printf("flags 0x%08u:\n", flag);
  if ((flag & 1) == 1)
    printf("%s, ", file_type[1]);
  if ((flag & 4) == 4)
    printf("%s, ", file_type[4]);
  if ((flag & 6) == 6)
    printf("%s, ", file_type[6]);
  if ((flag & 8) == 8)
    printf("%s", file_type[8]);
  printf("\n");
  return ;
}

static	void	flag(t_elf *elf)
{
  int		flag;
  int		ecx;

  flag = 0x0;
  ecx = -1;
  if (elf->ehdr->e_type == ET_EXEC)
    flag |= 1;
  if (elf->ehdr->e_type == ET_DYN)
    flag |= 6;
  while (++ecx < elf->ehdr->e_shnum)
    {
      if (elf->shdr[ecx].sh_type == SHT_SYMTAB)
	flag |= 4;
      else if (elf->shdr[ecx].sh_type == SHT_REL)
	flag |= 0;
      else if (elf->shdr[ecx].sh_type == SHT_HASH)
        flag |= 8;
    }
  print_flag(flag);
  return ;
}

static	void	flag64(t_elf *elf)
{
  int		flag;
  int		ecx;

  flag = 0x0;
  ecx = -1;
  if (elf->ehdr_64->e_type == ET_EXEC)
    flag |= 1;
  if (elf->ehdr_64->e_type == ET_DYN)
    flag |= 6;
  while (++ecx < elf->ehdr_64->e_shnum)
    {
      if (elf->shdr_64[ecx].sh_type == SHT_SYMTAB)
	flag |= 4;
      else if (elf->shdr_64[ecx].sh_type == SHT_REL)
	flag |= 0;
      else if (elf->shdr_64[ecx].sh_type == SHT_HASH)
        flag |= 8;
    }
  print_flag(flag);
  return ;
}

void		print_head(t_elf *elf, char *filename)
{
  char		*archi_type[9];

  archi_type[0] = "unknown";
  archi_type[1] =  "we32100";
  archi_type[2] =  "sparc";
  archi_type[3] =  "i386";
  archi_type[4] =  "m68000";
  archi_type[5] =  "m88000";
  archi_type[6] =  "i486";
  archi_type[7] =  "i860";
  archi_type[8] =  "r3000";
  archi_type[9] = NULL;
  elf_and_os(elf->ehdr, filename, elf->ehdr_64, elf->bit);
  if (elf->bit == true)
    {
      archi(elf->ehdr, NULL, archi_type, true);
      flag(elf);
      printf("start address 0x%08x\n\n", elf->ehdr->e_entry);
      return ;
    }
  archi64(elf->ehdr_64, NULL, archi_type, true);
  flag64(elf);
  printf("start address 0x%016x\n\n", (unsigned int)elf->ehdr_64->e_entry);
  return ;
}
