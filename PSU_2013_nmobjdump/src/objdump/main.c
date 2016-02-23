/*
** main.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:10:47 2014 Maxime renaud
** Last update Sun Mar 16 19:03:53 2014 Maxime renaud
*/

# include		<stdio.h>
# include		<stdlib.h>
# include		<string.h>
# include		<unistd.h>
# include		<sys/mman.h>
# include		"../../include/my_elf.h"

static	int		exec_file(char **av, int flag)
{
  int			ecx;
  t_elf			*elf;

  ecx = 0;
  while (av[++ecx])
    if (av[ecx][0] != '-')
      {
	if (!(elf = init(av[ecx], false, "objdump")))
	  return (-1);
	if ((flag & 1) == 1)
	  print_head(elf, av[ecx]);
	if ((flag & 2) == 2)
	  {
	    if (elf->bit == true)
	      arg_s(elf);
	    else
	      arg_s64(elf);
	  }
	if (munmap(elf->maddr, elf->mlen) == -1)
	  return (-1);
	free(elf);
      }
  return (0);
}

static	void		getflag(char **av,  int *flag)
{

  int			ecx;

  ecx = 0;
  *flag = 0;
  while (av[++ecx])
    {
      if (!strcmp(av[ecx], "-f"))
	*flag |= 1;
      else if (!strcmp(av[ecx], "-s"))
	*flag |= 2;
      else if (!strcmp(av[ecx], "-h"))
	*flag |= 4;
    }
  if (*flag == 0)
    {
      *flag |= 1;
      *flag |= 2;
    }
  return ;
}

int			main(int ac, char **av)
{
  int			flag;
  char			*tab[2];
  int			ecx;

  ecx = 0;
  tab[1] = "./a.out";
  tab[2] = NULL;
  getflag(av, &flag);
  while (av[++ecx])
    if (av[ecx][0] != '-')
      return (exec_file(av, flag));
  return (exec_file(&tab[0], flag));
}
