/*
** norme_epi.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Wed Mar 12 16:10:30 2014 Maxime renaud
** Last update Sun Mar 16 20:23:50 2014 Maxime renaud
*/

# include		<stdio.h>
# include		"../../include/my_elf.h"

void			archi(Elf32_Ehdr *ehdr, char *elf_type[3],
			      char *archi_type[9], t_bool flag)
{
  if (ehdr->e_machine < 9 && ehdr->e_ident[EI_CLASS] < 3)
    {
      if (flag == false)
	printf(" %s-%s\n",
	       elf_type[ehdr->e_ident[EI_CLASS]],
	   archi_type[ehdr->e_machine]);
      else
	printf("architecture: %s, ", archi_type[ehdr->e_machine]);
      return ;
    }
  return ;
}

void			archi64(Elf64_Ehdr *ehdr, char *elf_type[3],
			      char *archi_type[9], t_bool flag)
{
  if (ehdr->e_ident[EI_CLASS] < 3)
    {
      if (flag == false)
	printf(" %s-x86-64\n", elf_type[ehdr->e_ident[EI_CLASS]]);
      else
	{
	  if (ehdr->e_machine == 62)
	    printf("architecture: i386:x86-64, ");
	  else if (ehdr->e_machine < 9)
	    printf("architecture: %s, ", archi_type[ehdr->e_machine]);
	}
      return ;
    }
  return ;
}

void			elf_and_os(Elf32_Ehdr *ehdr, char *filename,
				   Elf64_Ehdr *ehdr64, t_bool flag)
{
  char			*elf_type[3];
  char			*archi_type[9];

  printf("\n%s:     file format", filename);
  elf_type[0] = "none";
  elf_type[1] = "elf32";
  elf_type[2] = "elf64";
  elf_type[3] = NULL;
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
  if (flag == true)
    archi(ehdr, elf_type, archi_type, false);
  else
    archi64(ehdr64, elf_type, archi_type, false);
  return ;
}
