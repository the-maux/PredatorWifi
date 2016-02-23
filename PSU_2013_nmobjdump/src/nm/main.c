/*
** main.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:09:57 2014 Maxime renaud
** Last update Sun May 11 21:01:11 2014 Maxime renaud
*/

# include		<stdio.h>
# include		<stdlib.h>
# include		<sys/mman.h>
# include		"../../include/my_elf.h"

static	void		print_list(t_form **list, t_bool flag)
{
  while (*list)
    {
      if (!(*list)->addr && (*list)->type != 'a' && flag == true)
	printf("         %c %s\n",
	       (*list)->type, (*list)->name);
      else if (!(*list)->addr64 && (*list)->type != 'a' && flag == false)
	printf("                 %c %s\n",
	       (*list)->type, (*list)->name);
      else if ((*list)->type != 'a' && flag == true)
	printf("%08x %c %s\n",
	       (*list)->addr, (*list)->type,
	       (*list)->name);
      else if ((*list)->type != 'a' && flag == false)
	printf("%016x %c %s\n",
	       (*list)->addr64, (*list)->type,
	       (*list)->name);
      list = &((*list)->next);
    }
  return ;
}

t_form			*exec_nm_for_tracing(char *filename)
{
  t_elf			*elf;
  t_form		*list_sym;

  if (!(elf = init(filename, "nm")))
    return (-1);
  else if (elf->bit == true &&
	   !(list_sym = create_list(elf, &(elf->sym_dyna))))
    return (-2);
  else if (elf->bit == false &&
	   !(list_sym = create_list64(elf, &(elf->sym_dyna64))))
    return (-2);
  sort(&list_sym, list_sym, elf->bit);
  print_list(&list_sym, elf->bit);
  if (munmap(elf->maddr, elf->mlen) == -1)
    return (-2);
  printf("Symbol table loaded\n");
  free(elf);
  return (list_sym);
}
